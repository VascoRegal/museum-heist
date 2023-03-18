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

    public void enqueue(OrdinaryThief thief) {
        thieves.enqueue(thief);
    }

    public OrdinaryThief dequeue() {
        return thieves.dequeue();
    }

    public int getRoomId() {
        return this.roomId;
    }

    public OrdinaryThief getClosest(OrdinaryThief thief) {
        int i, distance;
        OrdinaryThief[] thievesArray;
        OrdinaryThief closest;

        thievesArray = thieves.getArray();
        closest = null;

        for (i = 0; i < thievesArray.length; i++) {
            if (thief.getThiefId() != thievesArray[i].getThiefId()) {
                distance = thief.getPosition() - thievesArray[i].getPosition();
                if (closest == null || distance < closest.getPosition()) {
                    closest = thievesArray[i];
                }
            }
        }
        return closest;
    }
}
