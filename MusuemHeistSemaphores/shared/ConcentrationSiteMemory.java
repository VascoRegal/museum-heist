package shared;

import structs.MemQueue;
import structs.Semaphore;

import entities.OrdinaryThief;
import entities.ThiefState;

import java.util.logging.Logger;

import consts.HeistConstants;

public class ConcentrationSiteMemory {
    
    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );    

    private final OrdinaryThief [] ordinaryThieves;

    private final GeneralMemory generalMemory;

    private CollectionSiteMemory collectionSiteMemory;
    
    private final PartiesMemory partiesMemory;

    private final Semaphore access;

    private final Semaphore [] wait;

    private MemQueue<Integer> availableThieves;

    public ConcentrationSiteMemory(
        GeneralMemory generalMemory,
        PartiesMemory partiesMemory
        ) {
        ordinaryThieves = new OrdinaryThief [HeistConstants.NUM_THIEVES - 1];
        for (int i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            ordinaryThieves[i] = null;
        }
        this.generalMemory = generalMemory;
        this.collectionSiteMemory = null;
        this.partiesMemory = partiesMemory;
        this.availableThieves = new MemQueue<Integer>(new Integer[HeistConstants.NUM_THIEVES - 1]);
        access = new Semaphore();
        access.up();
        wait = new Semaphore [HeistConstants.NUM_THIEVES - 1];
        for (int i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            wait[i] = new Semaphore();
        }
    }

    public boolean amINeeded() {
        int ordinaryThiefId;

        access.down();
        ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getThiefId();
        if (ordinaryThieves[ordinaryThiefId] == null) {
            ordinaryThieves[ordinaryThiefId] = (OrdinaryThief) Thread.currentThread();
        }
        LOGGER.info("[OT" + ordinaryThiefId + "] Am I needed?");
        ordinaryThieves[ordinaryThiefId].setState(ThiefState.CONCENTRATION_SITE);
        generalMemory.setOrdinaryThiefState(ordinaryThiefId, ThiefState.CONCENTRATION_SITE);
        availableThieves.enqueue(ordinaryThiefId);
        access.up();
        collectionSiteMemory.notifyAvailable();
        wait[ordinaryThiefId].down();
        return true;
        
    }

    public boolean prepareExcursion() {
        int ordinaryThiefId;
        
        access.down();
        ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getThiefId();
        LOGGER.info("[OT" + ordinaryThiefId + "] Preparing for excursion!");
        collectionSiteMemory.confirmParty();
        ordinaryThieves[ordinaryThiefId].setState(ThiefState.CRAWLING_INWARDS);
        generalMemory.setOrdinaryThiefState(ordinaryThiefId, ThiefState.CRAWLING_INWARDS);
        access.up();
        partiesMemory.thiefReady(ordinaryThiefId, ordinaryThieves[ordinaryThiefId].getPartyId());
        return true;
    }

    public void addThiefToParty(int thiefId, int partyId) {
        access.down();
        ordinaryThieves[thiefId].setPartyId(partyId);
        partiesMemory.addThiefToParty(partyId, ordinaryThieves[thiefId]);
        access.up();
    }

    public int getNumAvailableThieves() {
        int numAvailableThieves;
        access.down();
        numAvailableThieves = availableThieves.size();
        access.up();
        return numAvailableThieves;
    }

    public int getAvailableThief() {
        int availableThief;
        access.down();
        availableThief = availableThieves.dequeue();
        wait[availableThief].up();
        access.up();
        return availableThief;
    }

    public void setCollectionSiteMemory(CollectionSiteMemory collectionSiteMemory) {
        this.collectionSiteMemory = collectionSiteMemory;
    }
}