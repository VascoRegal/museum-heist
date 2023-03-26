package shared;

import structs.MemQueue;
import structs.Semaphore;

import entities.OrdinaryThief;
import entities.ThiefState;

import consts.HeistConstants;


/**
 *  ConcentrationMemory class
 *
 *  Shared memory containing concentration site operations.
 *  to manage Ordinary Thieves 
 * 
 *  Public methods are controlled with an access semaphore
 *
 *  Synchronization points:
 *      - OrdinaryThief waiting to be called for a party
 */
public class ConcentrationSiteMemory {

    /**
     *  Reference to the Ordinary Thieves
     */

    private final OrdinaryThief [] ordinaryThieves;

    /**
     *   Reference to the General Memory
     */

    private final GeneralMemory generalMemory;

    /**
     *   Reference to the Collection Site Memory
     */

    private CollectionSiteMemory collectionSiteMemory;
    
    /**
     *   Reference to the Parties Memory
     */

    private final PartiesMemory partiesMemory;

    /**
     *   Semaphore to ensure mutual exlusion
     */    

    private final Semaphore access;

    /**
     *   Blocking semaphore for the OrdinaryThieves waiting
     *   for a party
     */

    private final Semaphore [] wait;

    /**
     *   Queue of available thieves
     */

    private MemQueue<Integer> availableThieves;


    /**
     *  Concentration Site memory instantiation.
     *
     *    @param generalMemory general memory reference
     *    @param partiesMemory parties memory reference
     */
    public ConcentrationSiteMemory(
        GeneralMemory generalMemory,
        PartiesMemory partiesMemory
        ) {
        ordinaryThieves = new OrdinaryThief [HeistConstants.NUM_THIEVES];
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThieves[i] = null;
        }
        this.generalMemory = generalMemory;
        this.collectionSiteMemory = null;
        this.partiesMemory = partiesMemory;
        this.availableThieves = new MemQueue<Integer>(new Integer[HeistConstants.NUM_THIEVES]);
        access = new Semaphore();
        access.up();
        wait = new Semaphore [HeistConstants.NUM_THIEVES];
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            wait[i] = new Semaphore();
        }
    }

    /**
     *  Am I needed
     *
     *  Thief blocks until notified by Master Thief to start a party
     *  or end of heist
     * 
     *      @return true, if needed for party
     *              false, if heist is finished
     */
    public boolean amINeeded() {
        int ordinaryThiefId;

        ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getThiefId();
        generalMemory.setOrdinaryThiefState(ordinaryThiefId, ThiefState.CONCENTRATION_SITE);
        access.down();
        if (ordinaryThieves[ordinaryThiefId] == null) {
            ordinaryThieves[ordinaryThiefId] = (OrdinaryThief) Thread.currentThread();
        }
        availableThieves.enqueue(ordinaryThiefId);
        access.up();
        collectionSiteMemory.notifyAvailable();
        generalMemory.logInternalState();
        wait[ordinaryThiefId].down();

        if (!generalMemory.isHeistInProgres()) {
            return false;
        }

        return true;
        
    }

    /**
     *  Preparing for excursion
     *
     *  Transitional state to confirm assembling of party
     *  Called after thief is needed
     * 
     *      @return id of thief's party
     */
    public int prepareExcursion() {
        int ordinaryThiefId;
        
        access.down();
        ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getThiefId();
        generalMemory.setOrdinaryThiefState(ordinaryThiefId, ThiefState.CRAWLING_INWARDS);
        access.up();
        collectionSiteMemory.confirmParty();
        return  ordinaryThieves[ordinaryThiefId].getPartyId();
    }


    /**
     *  Add a thief to the party
     *
     *  Called by the MasterThief to add theif to party
     *  Block the thief until he calls confirmParty()
     */
    public void addThiefToParty(int thiefId, int partyId) {
        access.down();
        ordinaryThieves[thiefId].setPartyId(partyId);
        access.up();
        partiesMemory.addThiefToParty(partyId, ordinaryThieves[thiefId]);
        wait[thiefId].up();
    }


    /**
     *  Get available thieves
     *
     *  Returns number of thieves in queue
     * 
     *      @return num of thieves in queue
     */
    public int getNumAvailableThieves() {
        int numAvailableThieves;
        access.down();
        numAvailableThieves = availableThieves.size();
        access.up();
        return numAvailableThieves;
    }


    /**
     *  Pop an ordinary thief from the available queue
     */
    public int getAvailableThief() {
        int availableThief;
        access.down();
        availableThief = availableThieves.dequeue();
        access.up();
        return availableThief;
    }

    /**
     *  Called by MasterThief
     *  
     *  Awakes all thieves blocked in amINeeded
     */
    public void notifyEndOfHeist() {
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++ ) {
            wait[i].up();
        }
    }

    /**
     *  Utility method to manage circular memory dependencies
     */
    public void setCollectionSiteMemory(CollectionSiteMemory collectionSiteMemory) {
        this.collectionSiteMemory = collectionSiteMemory;
    }
}