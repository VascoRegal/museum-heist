package shared;

import java.util.logging.Logger;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Party;
import structs.Semaphore;

public class PartiesMemory {

    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );    

    private final Semaphore access;

    private final Semaphore [][] proceed;

    private final Party [] parties;

    private final MusuemMemory musuemMemory;

    private int numActiveParties;

    public PartiesMemory(MusuemMemory musuemMemory) {
        this.musuemMemory = musuemMemory;
        proceed = new Semaphore[HeistConstants.MAX_NUM_PARTIES][HeistConstants.NUM_THIEVES];
        parties = new Party[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            parties[i] = null;
            for (int j = 0; j < HeistConstants.NUM_THIEVES; j++) {
                proceed[i][j] = new Semaphore();
            }
        }
        access = new Semaphore();
        access.up();
    }

    public int createParty() {
        access.down();
        int partyId = -1;
        for (int i = 0; i < parties.length; i++) {
            if (parties[i] == null) {
                parties[i] = new Party(i, musuemMemory.findNonClearedRoom().getId());
                partyId = i;
                numActiveParties++;
                access.up();
                return partyId;
            }
        }
        access.up();
        return partyId;
    }

    public int getNumActiveParties() {
        access.down();
        int num = numActiveParties;
        access.up();
        return num;
    }

    
    public void addThiefToParty(int partyId, OrdinaryThief thief) {
        access.down();
        parties[partyId].enqueue(thief);
        access.up();
    }

    public void thiefReady(int thiefId, int partyId) {
        proceed[partyId][thiefId].down();
        crawlingIn();
    }

    public void startParty(int partyId) {
        access.up();
        parties[partyId].dequeue();
        access.down();
        proceed[partyId][0].up();
    }

    public boolean crawlingIn() {
        access.down();
        OrdinaryThief currentThief, hThief, closestThief;
        int partyId;
        int distanceIncrement, roomLocation;
        
        currentThief = ((OrdinaryThief) Thread.currentThread());
        partyId = currentThief.getPartyId();

        LOGGER.info("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") moving in...");

        roomLocation = musuemMemory.getRoomLocation(parties[partyId].getRoomId());
        closestThief = parties[partyId].getClosest(currentThief);


        if ((closestThief.getPosition() + currentThief.getMaxDisplacement()) > HeistConstants.MAX_CRAWLING_DISTANCE) {
            distanceIncrement = HeistConstants.MAX_CRAWLING_DISTANCE - Math.abs(currentThief.getPosition() - closestThief.getPosition());
        } else {
            distanceIncrement = currentThief.getMaxDisplacement();
        }

        if ((currentThief.getPosition() + distanceIncrement) >= (roomLocation)) {
            return false;
        }

        currentThief.move(distanceIncrement);
        LOGGER.info("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " moved " + distanceIncrement + " units");
        parties[partyId].enqueue(currentThief);
        int nm = parties[partyId].dequeue().getThiefId();
        LOGGER.info("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " Telling OT" + nm + " to move.");
        proceed[partyId][nm].up();
        access.up();
        proceed[partyId][currentThief.getThiefId()].down();

        return true;
    }
}
