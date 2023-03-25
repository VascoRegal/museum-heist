package shared;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Party;
import entities.ThiefState;
import structs.Semaphore;

/**
 *  PartiesMemory class
 *
 *  Shared memory containing movement operations
 *
 *  Public methods are controlled with an access semaphore
 *  This region contains a number of sub regions equal to 
 *  the number of parties, controlled by an array of semaphores
 *
 *  Synchronization points:
 *      - Head Ordianry Thief waiting to start movement
 *      - Ordinary Thief waiting to move
 */
public class PartiesMemory {  

     /*
     *   Array of blocking semaphores for the access to
     *   a subregion, indexed by partyId
     */

    private final Semaphore [] partyAccess;

     /*
     *   General access for non movement operations
     */

    private final Semaphore generalAccess;

     /*
     *   2D Array of blocking semaphores for the access to
     *   a thief in a party
     */

    private final Semaphore [][] proceed; 

    /**
     *   Reference to parties
     */

    private final Party [] parties;

    /**
     *   Reference to museuem morery
     */

    private final MusuemMemory musuemMemory;

    /**
     *   Reference to general memory
     */

    private final GeneralMemory generalMemory;

    /**
     *   number of active parties
     */

    private int numActiveParties;


    /**
     *  Parties memory instantiation.
     *
     *    @param generalMemory general memory reference
     *    @param concentrationSiteMemory concentration memory reference
     *    @param museumMemory museum memory reference
     *    @param partiesMemory parties memory reference
     */

    public PartiesMemory(MusuemMemory musuemMemory, GeneralMemory generalMemory) {
        this.musuemMemory = musuemMemory;
        this.generalMemory = generalMemory;
        proceed = new Semaphore[HeistConstants.MAX_NUM_PARTIES][HeistConstants.NUM_THIEVES];
        parties = new Party[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            parties[i] = null;
            for (int j = 0; j < HeistConstants.NUM_THIEVES; j++) {
                proceed[i][j] = new Semaphore();
            }
        }
        generalMemory.setParties(parties);
        partyAccess = new Semaphore[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            partyAccess[i] = new Semaphore();
            partyAccess[i].up();
        }

        generalAccess = new Semaphore();
        generalAccess.up();
    }

    /**
     *  Party creation on memory
     *  Tries to create a party otherwise raise an exception 
     * 
     *    @param roomId id of the target room
     *    @return id of the created party
     */

    public int createParty(int roomId) {
        generalAccess.down();
        // TODO: raise exception on invalid party
        int partyId = -1;
        for (int i = 0; i < parties.length; i++) {
            if (parties[i] == null) {
                parties[i] = new Party(i, roomId);
                partyId = i;
                numActiveParties++;
                generalAccess.up();
                generalMemory.setParties(parties);
                return partyId;
            }
        }
        generalAccess.up();
        return partyId;
    }

    /**
     *  Party deletion
     *  Resets members and semaphores
     * 
     *    @param partyId id of the target party
     */

    public void disbandParty(int partyId) {
        generalAccess.down();
        // generalMemorylogInternalState();
        parties[partyId] = null;
        numActiveParties--;
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            proceed[partyId][i].release();
        }
        generalAccess.up();
        generalMemory.setParties(parties);
    }

    /**
     *  Get the current num of active parties
     *  Called by Master Thief to decide what to do
     * 
     *    @return num of parties
     */

    public int getNumActiveParties() {
        generalAccess.down();
        int num = numActiveParties;
        generalAccess.up();
        return num;
    }

    
    /**
     *  Assigns thief to party
     * 
     *    @param partyId id of the target party
     *    @param thief  thief to be added
     */

    public void addThiefToParty(int partyId, OrdinaryThief thief) {
        generalAccess.down();
        parties[partyId].join(thief);
        generalAccess.up();
    }

    /**
     *  Notify head of party to start movement
     * 
     *    @param partyId id of the target party  
     */

    public void startParty(int partyId) {
        int headId;

        generalAccess.down();
        // generalMemorylogInternalState();
        headId = parties[partyId].getFirst();
        proceed[partyId][headId].up();
        generalAccess.up();
    }

    
    /**
     *  Notify head of party to start reverse movement
     * 
     *    @param partyId id of the target party  
     */

    public void reverseDirection(int partyId) {
        int headId;

        partyAccess[partyId].down();
        // generalMemorylogInternalState();
        headId = parties[partyId].getFirst();
        partyAccess[partyId].up();
        proceed[partyId][headId].up();
    }

    /**
     *  Crawling in movement
     * 
     *    Ordinary Thief blocks and waits to be awaken either
     *    by the start of the movement by the MasterThief or
     *    by another OridnaryThief giving him his turn
     * 
     *    If thief can move, do the best possible move
     *    Else, get the closest thief to him, notify him and
     *    go back to blocking
     * 
     *    If after the movement thief is at his goal, transition state
     */

    public int crawlingIn() {
        OrdinaryThief currentThief, closestThief;
        int partyId;
        int roomLocation;

        currentThief = ((OrdinaryThief) Thread.currentThread());
        generalMemory.setOrdinaryThiefState(currentThief.getThiefId(), ThiefState.CRAWLING_INWARDS);
        partyId = currentThief.getPartyId();
        roomLocation = musuemMemory.getRoomLocation(parties[partyId].getRoomId());
        while (true) {
            proceed[partyId][currentThief.getThiefId()].down();
            partyAccess[partyId].down();
            // generalMemorylogInternalState();
            while (parties[partyId].canIMove() && currentThief.getPosition() < roomLocation) {
                parties[partyId].move();
            }
            closestThief = parties[partyId].getNext();

            if (closestThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {
                proceed[partyId][closestThief.getThiefId()].up();
            }

            if (currentThief.getPosition() >= roomLocation) {
                currentThief.setPosition(roomLocation);
                generalMemory.setOrdinaryThiefState(currentThief.getThiefId(), ThiefState.AT_A_ROOM);
                break;
            }
            partyAccess[partyId].up();
        }
        partyAccess[partyId].up();
        return parties[partyId].getRoomId();
    }


    /**
     *   Similar to crawling in but with different end
     *      postions and movement increments
     */
    public boolean crawlingOut() {
        OrdinaryThief currentThief, closestThief;
        int partyId, siteLocation;
        
        currentThief = ((OrdinaryThief) Thread.currentThread());
        partyId = currentThief.getPartyId();

        generalMemory.setOrdinaryThiefState(currentThief.getThiefId(), ThiefState.CRAWLING_OUTWARDS);
        siteLocation = 0;

        if ( currentThief.getThiefId() == parties[partyId].getLast() ) {
            reverseDirection(partyId);
        }

        while (true) {
            proceed[partyId][currentThief.getThiefId()].down();
            partyAccess[partyId].down();

            while (parties[partyId].canIMove() && currentThief.getPosition() > siteLocation) {
                parties[partyId].move();
            }
            closestThief = parties[partyId].getNext();
            proceed[partyId][closestThief.getThiefId()].up();
            if (currentThief.getPosition() <= siteLocation) {
                currentThief.setPosition(siteLocation);
                generalMemory.setOrdinaryThiefState(currentThief.getThiefId(), ThiefState.COLLECTION_SITE);
                break;
            }
            partyAccess[partyId].up();
        }
        partyAccess[partyId].up();
        return false;
    }
}
