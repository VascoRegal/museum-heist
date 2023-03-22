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
        parties[partyId].join(thief);
        access.up();
    }

    public void startParty(int partyId) {
        proceed[partyId][0].up();
    }

    public boolean crawlingIn() {
        OrdinaryThief currentThief, closestThief;
        int partyId;
        int roomLocation;
        
        currentThief = ((OrdinaryThief) Thread.currentThread());
        partyId = currentThief.getPartyId();
        roomLocation = musuemMemory.getRoomLocation(parties[partyId].getRoomId());
        while (true) {
            proceed[partyId][currentThief.getThiefId()].down();
            access.up();
            System.out.println("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") moving in...");
            while (parties[partyId].canIMove(currentThief)) {
                System.out.println("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") can Move!");
                parties[partyId].move(currentThief);
                System.out.println("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") moved to position " + currentThief.getPosition());
            }
            System.out.println("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") Can no longer move");
            closestThief = parties[partyId].getNext(currentThief);
            System.out.println("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " (MD="+ currentThief.getMaxDisplacement() + ") Notifiying OT" + closestThief.getThiefId() + " to start moving");
            proceed[partyId][closestThief.getThiefId()].up();
            if (currentThief.getPosition() >= roomLocation) {
                System.out.println("perdi o jogo");
                break;
            }
            access.down();
        }
        return true;
    }

    public boolean crawlingOut() {
        OrdinaryThief currentThief, hThief, closestThief;
        int partyId;
        int distanceIncrement, roomLocation;
        
        currentThief = ((OrdinaryThief) Thread.currentThread());
        partyId = currentThief.getPartyId();

        // LOGGER.info("[PARTY " + partyId + "] OT" + currentThief.getThiefId() + " CRAWLING OUT.");
        return false;
    }
}
