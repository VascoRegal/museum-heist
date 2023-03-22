package entities;

import consts.HeistConstants;
import structs.MemPartyArray;
import structs.MemQueue;

public class Party {
    
    private final int id;

    private final int roomId;

    private final  MemPartyArray partyArray;

    public Party(int id, int roomId) {
        this.id = id;
        this.roomId = roomId;

        OrdinaryThief[] thieves = new OrdinaryThief[HeistConstants.PARTY_SIZE];
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++ ) {
            thieves[i] = null;
        }

        partyArray = new MemPartyArray(thieves);
    }

    public int getRoomId() {
        return this.roomId;
    }

    public OrdinaryThief getNext(OrdinaryThief ordinaryThief) {
        return partyArray.getNext(ordinaryThief);
    }

    public void join(OrdinaryThief ordinaryThief) {
        partyArray.join(ordinaryThief);
    }

    public boolean canIMove(OrdinaryThief ordinaryThief) {
        return partyArray.canMove(ordinaryThief);
    }

    public void move(OrdinaryThief ordinaryThief) {
        partyArray.bestMove(ordinaryThief);
    }
}
