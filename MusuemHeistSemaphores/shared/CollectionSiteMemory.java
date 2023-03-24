package shared;

import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import entities.Party;
import entities.RoomState;
import entities.ThiefState;
import structs.MemQueue;
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

    private final Semaphore[] collect;

    private final MemQueue<OrdinaryThief> collectQueue;

    private final int [] partyMembers;

    private final int [] partyRooms;

    private final boolean [] clearedRooms;

    private int totalClearedRooms;

    private int totalPaintings;



    public CollectionSiteMemory(
        GeneralMemory generalMemory, 
        ConcentrationSiteMemory concentrationSiteMemory, 
        MusuemMemory musuemMemory,
        PartiesMemory partiesMemory
    ) {
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
        collect = new Semaphore[HeistConstants.NUM_THIEVES];
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            collect[i] = new Semaphore();
        }
        partyMembers = new int[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            partyMembers[i] = 0;
        }
        partyRooms = new int[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            partyRooms[i] = -1;
        }
        clearedRooms = new boolean[HeistConstants.NUM_ROOMS];
        for (int i = 0; i < HeistConstants.NUM_ROOMS; i++) {
            clearedRooms[i] = false;
        }
        collectQueue = new MemQueue<OrdinaryThief>(new OrdinaryThief[HeistConstants.NUM_THIEVES]);
        totalClearedRooms = 0;
        totalPaintings = 0;
    }

    public boolean startOperations() {
        access.down();
        // LOGGER.info("[MT] Starting Operations...");
        if (masterThief == null) {
            masterThief = (MasterThief) Thread.currentThread();
        }
        masterThief.setThiefState(ThiefState.PLANNING_THE_HEIST);
        generalMemory.setMasterThiefState(ThiefState.PLANNING_THE_HEIST);
        access.up();
        return true;
    }

    public char appraiseSit() {
        char action;
        int numAvailableThieves, numActiveParties;
        generalMemory.logInternalState();
        access.down();
        // LOGGER.info("[MT] Appraise Sitting");

        masterThief.setThiefState(ThiefState.DECIDING_WHAT_TO_DO);
        generalMemory.setMasterThiefState(ThiefState.DECIDING_WHAT_TO_DO);
        numActiveParties = partiesMemory.getNumActiveParties();

        if (totalClearedRooms == HeistConstants.NUM_ROOMS && numActiveParties == 0) 
        {
            generalMemory.finishHeist();
            return 's';
        }

        if (numActiveParties == HeistConstants.MAX_NUM_PARTIES ||
            (musuemMemory.findNonClearedRoom() == null && numActiveParties == 1)    
        ) 
        {
            action = 'r';
        }
        else {
            numAvailableThieves = 0;
            while (numAvailableThieves < HeistConstants.PARTY_SIZE) {
                wait.down();
                numAvailableThieves += 1;
            }
            action = 'p';
        }
        access.up();

        return action;
    }

    public int prepareAssaultParty() {
        int numConfirmedThieves, partyId, availableThief, roomId;
        //LOGGER.info("[MT] Preparing Assault Party");
        masterThief.setThiefState(ThiefState.ASSEMBLING_A_GROUP);
        generalMemory.setMasterThiefState(ThiefState.ASSEMBLING_A_GROUP);

        roomId = musuemMemory.findNonClearedRoom().getId();
        partyId = partiesMemory.createParty(roomId);
        partyRooms[partyId] = roomId;

        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            availableThief = concentrationSiteMemory.getAvailableThief();
            concentrationSiteMemory.addThiefToParty(availableThief, partyId);
            partyMembers[partyId] += 1;
        }

        numConfirmedThieves = 0;
        while (numConfirmedThieves < HeistConstants.PARTY_SIZE) {
            assemble.down();
            numConfirmedThieves += 1;
            // LOGGER.info("[MT] Thief confirmed (" + numConfirmedThieves + ")");
        }
        musuemMemory.markRoomAs(roomId, RoomState.IN_PROGRESS);
        return partyId;
    }

    public boolean sendAssaultParty(int partyId) {
        partiesMemory.startParty(partyId);
        return true;
    }

    public boolean takeARest() {
        access.down();
        // LOGGER.info("[MT] Taking a rest.");
        masterThief.setThiefState(ThiefState.WAITING_FOR_GROUP_ARRIVAL);
        generalMemory.setMasterThiefState(ThiefState.WAITING_FOR_GROUP_ARRIVAL);    
        access.up();
        arrival.down();
        return true;
    }

    public boolean collectACanvas() {
        OrdinaryThief handingThief;
        int partyId, roomId;

        access.down();

        handingThief = collectQueue.dequeue();
        partyId = handingThief.getPartyId();
        roomId = partyRooms[partyId];

        if (handingThief.hasCanvas()) {
            handingThief.handleCanvas();
            totalPaintings++;
        } else {
            if (!clearedRooms[roomId]) {
                musuemMemory.markRoomAs(roomId, RoomState.COMPLETED);
                clearedRooms[roomId] = true;
                totalClearedRooms++;
            }
        }

        partyMembers[partyId]--;

        if (partyMembers[partyId] == 0) {
            if (!clearedRooms[roomId]) {
                musuemMemory.markRoomAs(roomId, RoomState.AVAILABLE);
            }
            partiesMemory.disbandParty(partyId);
        }

        collect[handingThief.getThiefId()].up();
        access.up();
        return true;
    }

    public boolean handACanvas() {
        OrdinaryThief currentThief;

        currentThief = ((OrdinaryThief) Thread.currentThread());
        collectQueue.enqueue(currentThief);
        arrival.up();
        collect[currentThief.getThiefId()].down();
        currentThief.setPartyId(-1);
        return true;
    }

    public boolean sumUpResults() {
        int numConfirmedThieves;

        masterThief.setThiefState(ThiefState.PRESENTING_THE_REPORT);
        generalMemory.setMasterThiefState(ThiefState.PRESENTING_THE_REPORT);
        concentrationSiteMemory.notifyEndOfHeist();
        numConfirmedThieves = 0;
        while (numConfirmedThieves < HeistConstants.NUM_THIEVES) {
            wait.down();
            numConfirmedThieves += 1;
        }

        System.out.println("Total paintings: " + totalPaintings);

        return true;
    }

    public void notifyAvailable() {
        wait.up();
    }

    public void confirmParty() {
        assemble.up();
    }
}
