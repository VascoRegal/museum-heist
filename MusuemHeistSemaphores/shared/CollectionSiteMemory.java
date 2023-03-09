package shared;

import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.Party;
import entities.ThiefState;
import structs.Semaphore;

public class CollectionSiteMemory {

    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );    

    private MasterThief masterThief;

    private final GeneralMemory generalMemory;

    private final ConcentrationSiteMemory concentrationSiteMemory;

    private final MusuemMemory musuemMemory;

    private final PartiesMemory partiesMemory;

    private final Semaphore access;

    private final Semaphore assemble;

    private final Semaphore arrival;

    private final Semaphore wait;

    public CollectionSiteMemory(
        GeneralMemory generalMemory, 
        ConcentrationSiteMemory concentrationSiteMemory, 
        MusuemMemory musuemMemory,
        PartiesMemory partiesMemory) {
        masterThief = null;
        this.generalMemory = generalMemory;
        this.concentrationSiteMemory = concentrationSiteMemory;
        this.musuemMemory = musuemMemory;
        this.partiesMemory = partiesMemory;
        access = new Semaphore();
        access.up();
        wait = new Semaphore();
        assemble = new Semaphore();
        arrival = new Semaphore();
    }

    public boolean startOperations() {
        access.down();
        LOGGER.info("[MT] Starting Operations...");
        if (masterThief == null) {
            masterThief = (MasterThief) Thread.currentThread();
        }
        masterThief.setState(ThiefState.DECIDING_WHAT_TO_DO);
        generalMemory.setMasterThiefState(ThiefState.DECIDING_WHAT_TO_DO);
        while (generalMemory.isHeistInProgres()) {
            if (partiesMemory.getNumActiveParties() == HeistConstants.MAX_NUM_PARTIES) {
                takeARest();
            }
            if (appraiseSit()) {
                prepareAssaultParty();
            }
        }
        sumUpResults();
        access.up();
        return true;
    }

    public boolean appraiseSit() {
        LOGGER.info("[MT] Appraise Sitting");
        
        int numAvailableThieves;

        numAvailableThieves = 0;
        while (numAvailableThieves < HeistConstants.PARTY_SIZE) {
            wait.down();
            numAvailableThieves += 1;
        }
        //wait.down();
        return true;
    }

    public boolean prepareAssaultParty() {
        int numConfirmedThieves, partyId, availableThief;

        LOGGER.info("[MT] Preparing Assault Party");
        masterThief.setState(ThiefState.ASSEMBLING_A_GROUP);
        generalMemory.setMasterThiefState(ThiefState.ASSEMBLING_A_GROUP);
        partyId = partiesMemory.createParty();
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            availableThief = concentrationSiteMemory.getAvailableThief();
            concentrationSiteMemory.addThiefToParty(availableThief, partyId);
        }
        numConfirmedThieves = 0;
        while (numConfirmedThieves < HeistConstants.PARTY_SIZE) {
            assemble.down();
            numConfirmedThieves += 1;
            LOGGER.info("[MT] Thief confirmed (" + numConfirmedThieves + ")");
        }
        sendAssaultParty(partyId);
        return true;
    }

    public boolean sendAssaultParty(int partyId) {
        LOGGER.info("[MT] Sending assault party");
        partiesMemory.startParty(partyId);
        masterThief.setState(ThiefState.DECIDING_WHAT_TO_DO);
        generalMemory.setMasterThiefState(ThiefState.DECIDING_WHAT_TO_DO);
        return true;
    }

    public boolean takeARest() {
        LOGGER.info("[MT] Taking a rest.");
        masterThief.setState(ThiefState.WAITING_FOR_GROUP_ARRIVAL);
        generalMemory.setMasterThiefState(ThiefState.WAITING_FOR_GROUP_ARRIVAL);    
        arrival.down();
        return true;
    }

    public boolean collectACanvas() {
        return true;
    }

    public boolean sumUpResults() {
        return true;
    }

    public void notifyAvailable() {
        wait.up();
    }

    public void confirmParty() {
        assemble.up();
    }

    public void notifyArrival() {
        arrival.up();
    }

}
