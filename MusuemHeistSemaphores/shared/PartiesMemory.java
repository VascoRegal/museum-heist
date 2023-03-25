package shared;

import java.util.logging.Logger;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Party;
import entities.ThiefState;
import structs.Semaphore;

public class PartiesMemory {

    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );    

    private final Semaphore [] partyAccess;

    private final Semaphore generalAccess;

    private final Semaphore [][] proceed; 

    private final Party [] parties;

    private final MusuemMemory musuemMemory;

    private final GeneralMemory generalMemory;

    private int numActiveParties;

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

    public int createParty(int roomId) {
        generalAccess.down();
        generalMemory.logInternalState();
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

    public void disbandParty(int partyId) {
        generalAccess.down();
        generalMemory.logInternalState();
        parties[partyId] = null;
        numActiveParties--;
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            proceed[partyId][i].release();
        }
        generalAccess.up();
        generalMemory.setParties(parties);
    }

    public int getNumActiveParties() {
        generalAccess.down();
        int num = numActiveParties;
        generalAccess.up();
        return num;
    }

    
    public void addThiefToParty(int partyId, OrdinaryThief thief) {
        generalAccess.down();
        parties[partyId].join(thief);
        generalAccess.up();
    }

    public void startParty(int partyId) {
        int headId;

        generalAccess.down();
        generalMemory.logInternalState();
        headId = parties[partyId].getFirst();
        proceed[partyId][headId].up();
        generalAccess.up();
    }

    public void reverseDirection(int partyId) {
        int headId;

        partyAccess[partyId].down();
        generalMemory.logInternalState();
        headId = parties[partyId].getFirst();
        partyAccess[partyId].up();
        proceed[partyId][headId].up();
    }

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
            generalMemory.logInternalState();
            while (parties[partyId].canIMove(currentThief) && currentThief.getPosition() < roomLocation) {
                parties[partyId].move(currentThief);
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
            generalMemory.logInternalState();

            while (parties[partyId].canIMove(currentThief) && currentThief.getPosition() > siteLocation) {
                parties[partyId].move(currentThief);
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
