package shared;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Party;
import structs.Semaphore;

public class PartiesMemory {

    private final Semaphore access;

    private final Semaphore [][] proceed;

    private final Party [] parties;

    private final MusuemMemory musuemMemory;

    public PartiesMemory(MusuemMemory musuemMemory) {
        this.musuemMemory = musuemMemory;
        proceed = new Semaphore[HeistConstants.MAX_NUM_PARTIES][HeistConstants.PARTY_SIZE];
        parties = new Party[HeistConstants.MAX_NUM_PARTIES];
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            for (int j = 0; j < HeistConstants.PARTY_SIZE; j++) {
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
            }
        }
        access.up();
        return partyId;
    }

    public int getNumActiveParties() {
        int count = 0;
        access.down();
        for (int i = 0; i < parties.length; i++) {
            if (parties[i] != null) {
                count++;
            }
        }
        access.up();
        return count;
    }

    public void addThiefToParty(int partyId, OrdinaryThief thief) {
        access.down();
        parties[partyId].addThief(thief);
        access.up();
    }

    public void thiefReady(int thiefId, int partyId) {
        proceed[partyId][thiefId].down();
        crawlingIn();
    }

    public void startParty(int partyId) {
        proceed[partyId][0].up();
    }

    public boolean crawlingIn() {
        OrdinaryThief currentThief, hThief;
        int partyId;
        

        currentThief = ((OrdinaryThief) Thread.currentThread());
        partyId = currentThief.getPartyId();
        hThief = parties[partyId].getHead();

        if (currentThief.getThiefId() == hThief.getThiefId()) {
            
        } else {

        }


        return true;
    }
}
