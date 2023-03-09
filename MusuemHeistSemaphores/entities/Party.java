package entities;

import consts.HeistConstants;
import structs.MemQueue;

public class Party {
    
    private final int id;

    private final int roomId;

    private final MemQueue<OrdinaryThief> thieves;

    public Party(int id, int roomId) {
        this.id = id;
        this.roomId = roomId;
        thieves = new MemQueue<OrdinaryThief>(new OrdinaryThief[HeistConstants.PARTY_SIZE]);
    }

    public void addThief(OrdinaryThief thief) {
        thieves.enqueue(thief);
    }

    public OrdinaryThief getHead() {
        return thieves.peek();
    }
}
