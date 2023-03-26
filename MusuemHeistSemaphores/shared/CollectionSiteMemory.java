package shared;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import entities.RoomState;
import entities.ThiefState;
import structs.MemException;
import structs.MemQueue;
import structs.Semaphore;

/**
 *  CollectionMemory class
 *
 *  Shared memory containing collection site operations.
 *  Contains up to date information regarding the heist,
 *  parties and collection of canvas.
 *
 *  Public methods are controlled with an access semaphore
 *
 *  Synchronization points:
 *      - MasterThief waiting for thieves to form a party
 *    or sum up the results
 *      - MasterThief waiting confirmation when assembling
 *    a party.
 *      - MasterThief waiting for Ordinary Thieves arrival
 *    to collect canvas
 *      - OrdinaryThief[i] waiting for MasterThief to pick
 *    his canvas
 *      - MasterThief waiting to sum results
 */
public class CollectionSiteMemory { 

    /**
     *  Reference to the MasterThief
     */

     private MasterThief masterThief;

     /**
     *   Reference to the General Memory
     */
 
     private final GeneralMemory generalMemory;
 
     /**
     *   Reference to the Concentration Site Memory
     */
 
     private final ConcentrationSiteMemory concentrationSiteMemory;
 
     /**
     *   Reference to the Museum Memory
     */
 
     private final MuseumMemory museumMemory;
 
     /**
     *   Reference to the Parties Memory
     */
 
     private final PartiesMemory partiesMemory;
 
     /**
     *   Semaphore to ensure mutual exlusion
     */
 
     private final Semaphore access;
 
     /**
     *   Blocking semaphore for the MasterThief assembling a group
     */
 
     private final Semaphore assemble;
 
     /**
     *   Blocking semaphore for the MasterThief waiting for arrival
     */
 
     private final Semaphore arrival;
 
     /**
     *   Blocking semaphore for the MasterThief waiting available thieves
     */
 
     private final Semaphore wait;
 
     /*
     *   Array of blocking semaphores for the handing thieves
     */
 
     private final Semaphore[] collect;
 
 
     /*
     *   Queue with the thieves threds waiting for collection
     */
 
     private final MemQueue<OrdinaryThief> collectQueue;
 
     /*
     *   Index of parties current count
     */
 
     private final int [] partyCounts;
 
     /**
     *   Index of parties current target rooms
     */
 
     private final int [] partyRooms;
 
     /**
     *   Index of cleared rooms
     */
 
     private final boolean [] clearedRooms;
 
     /**
     *   Total number of cleared rooms
     */
 
     private int totalClearedRooms;
 
     /**
     *   Total number of rolled paintings
     */

    private int totalPaintings;


    /**
     *  Collection Site memory instantiation.
     *
     *    @param generalMemory general memory reference
     *    @param concentrationSiteMemory concentration memory reference
     *    @param museumMemory museum memory reference
     *    @param partiesMemory parties memory reference
     */

    public CollectionSiteMemory(
        GeneralMemory generalMemory, 
        ConcentrationSiteMemory concentrationSiteMemory, 
        MuseumMemory museumMemory,
        PartiesMemory partiesMemory
    ) {
        masterThief = null;
        this.generalMemory = generalMemory;
        this.concentrationSiteMemory = concentrationSiteMemory;
        this.museumMemory = museumMemory;
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
        partyCounts = new int[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            partyCounts[i] = 0;
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

    /**
     *  Start Operations
     *
     *  First operation called on the master thief lifecycle.
     *  Transitional state
     *
     */
    public boolean startOperations() {
        access.down();

        if (masterThief == null) {
            masterThief = (MasterThief) Thread.currentThread();
        }
        generalMemory.setMasterThiefState(ThiefState.PLANNING_THE_HEIST);
        access.up();
        return true;
    }


    /**
     *  Operation appraiseSit
     *
     *  This operation, called by the MasterThief decides which action to do next
     *  based on the current state of the heist.
     *
     *  Blocks if there are not enough thieves to create a party or end the heist
     *  Proceeds otherwise
     *
     *      @return 'p' if there are available thieves to form a party
     *      @return 'r' if there all parties are active or if only one party is active
     *  but no rooms are available
     *      @return 's' if heist is finished
     */
    public char appraiseSit() {
        char action;
        int numAvailableThieves, numActiveParties;

        generalMemory.setMasterThiefState(ThiefState.DECIDING_WHAT_TO_DO);
        numActiveParties = partiesMemory.getNumActiveParties();
        access.down();
        if (totalClearedRooms == HeistConstants.NUM_ROOMS && numActiveParties == 0) 
        {
            access.up();
            generalMemory.finishHeist(totalPaintings);
            return 's';
        }
        
        if (numActiveParties == HeistConstants.MAX_NUM_PARTIES ||
            (museumMemory.findNonClearedRoom() == null && numActiveParties == 1)    
        ) 
        {
            action = 'r';
        }
        else {
            numAvailableThieves = 0;
            while (numAvailableThieves < HeistConstants.PARTY_SIZE) {
                access.up();
                wait.down();
                numAvailableThieves += 1;
                access.down();
            }
            action = 'p';
        }
        access.up();

        return action;
    }


    /**
     *  Preparing a party
     *
     *  MasterThief finds an available room and creates a party.
     *  Then fetches 3 thieves from the queue in the concentration site
     *  and blocks for each.
     * 
     *  Once every party thief is awaken, proceed
     *  
     *      @return id of created party
     */
    public int prepareAssaultParty() {
        int numConfirmedThieves, partyId, availableThief, roomId;
        //LOGGER.info("[MT] Preparing Assault Party");
        access.down();
        generalMemory.setMasterThiefState(ThiefState.ASSEMBLING_A_GROUP);

        partyId = -1;
        roomId = museumMemory.findNonClearedRoom().getId();
        try {
            partyId = partiesMemory.createParty(roomId);
        } catch (MemException e) {
            e.printStackTrace();
            access.up();
            System.exit(1);
        }
        partyRooms[partyId] = roomId;

        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            access.up();
            availableThief = concentrationSiteMemory.getAvailableThief();
            concentrationSiteMemory.addThiefToParty(availableThief, partyId);
            access.down();
            partyCounts[partyId] += 1;
        }

        numConfirmedThieves = 0;
        while (numConfirmedThieves < HeistConstants.PARTY_SIZE) {
            access.up();
            assemble.down();
            numConfirmedThieves += 1;
            access.down();
        }
        museumMemory.markRoomAs(roomId, RoomState.IN_PROGRESS);
        access.up();
        return partyId;
    }

    /**
     *  Send assault party
     *
     *  Notify the head thief of the party
     * 
     *      @param partyId id of the party
     *      @return true when party starts
     */
    public boolean sendAssaultParty(int partyId) {
        partiesMemory.startParty(partyId);
        return true;
    }


    /**
     *  Take a rest
     *
     *  MasterThief awaits for the arrival of a thief
     *  with a canvas
     */
    public void takeARest() {
        access.down();
        generalMemory.setMasterThiefState(ThiefState.WAITING_FOR_GROUP_ARRIVAL);    
        access.up();
        arrival.down();
    }

    /**
     *  Canvas collection
     *
     *  Triggered by the arrival of a thief
     * 
     *  Get the thief from the collection queue, and
     *  collect his canvas.
     * 
     *  If a thief has no canvas, mark his party's
     *  room as complete.
     * 
     *  Then, awake the thief
     *  
     *      @return id of created party
     */
    public boolean collectACanvas() {
        OrdinaryThief handingThief;
        int partyId, roomId;

        access.down();

        handingThief = collectQueue.dequeue();
        partyId = handingThief.getPartyId();
        roomId = partyRooms[partyId];

        if (handingThief.hasCanvas()) {
            handingThief.removeCanvas();
            totalPaintings++;
        } else {
            if (!clearedRooms[roomId]) {
                access.up();
                museumMemory.markRoomAs(roomId, RoomState.COMPLETED);
                access.down();
                clearedRooms[roomId] = true;
                totalClearedRooms++;
            }
        }

        partyCounts[partyId]--;

        if (partyCounts[partyId] == 0) {
            if (!clearedRooms[roomId]) {
                access.up();
                museumMemory.markRoomAs(roomId, RoomState.AVAILABLE);
                access.down();
            }
            access.up();
            partiesMemory.disbandParty(partyId);
            access.down();
        }
        access.up();
        collect[handingThief.getThiefId()].up();
        return true;
    }


    /**
     *  Canvas handing
     *
     *  Called by the ordinary thieves when they return from
     *  a room
     * 
     *  Ordinary thief enters the collection queue and awakes
     *  MasterThief to collect his canvas
     *  
     */
    public void handACanvas() {
        OrdinaryThief currentThief;

        access.down();
        currentThief = ((OrdinaryThief) Thread.currentThread());
        collectQueue.enqueue(currentThief);
        access.up();
        arrival.up();
        collect[currentThief.getThiefId()].down();
    }


    /**
     *  Sum up results
     *
     *  Called by the Master Thief when the heist ends 
     *  Presents the results after all thieves are ready.
     */
    public void sumUpResults() {
        int numConfirmedThieves;

        generalMemory.setMasterThiefState(ThiefState.PRESENTING_THE_REPORT);
        concentrationSiteMemory.notifyEndOfHeist();
        access.down();
        numConfirmedThieves = 0;
        while (numConfirmedThieves < HeistConstants.NUM_THIEVES) {
            access.up();
            wait.down();
            access.down();
            numConfirmedThieves += 1;
        }
        System.out.println("total : " + totalPaintings);
        access.up();
    }

    /**
     *  Notify ordinary thief is available
     *
     *  Called by the ordinary thieves from
     *  the concentration site
     */
    public void notifyAvailable() {
        access.down();
        wait.up();
        access.up();
    }

    /**
     *  Notify ordinary thief is ready for party
     *
     *  Called by the ordinary thieves from
     *  the concentration site
     */
    public void confirmParty() {
        access.down();
        assemble.up();
        access.up();
    }
}
