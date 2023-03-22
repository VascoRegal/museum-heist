package structs;

import consts.HeistConstants;
import entities.OrdinaryThief;

public class MemPartyArray {
    
    private OrdinaryThief head;

    private OrdinaryThief tail;

    private OrdinaryThief [] data;

    public MemPartyArray(OrdinaryThief[] data) {
        this.data = data;
    }

    public void join(OrdinaryThief thief) {
        int insertIdx;

        if (head == null) {
            head = thief;
            data[0] = thief;
            return;
        }

        insertIdx = -1;
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (data[i] == null) {
                data[i] = thief;
                insertIdx = i;
                break;
            }
        }

        if (insertIdx == (data.length - 1) && tail == null) {
            tail = thief;
        }
    }

    public OrdinaryThief getNext(OrdinaryThief thief) {
        if (thief.getThiefId() == tail.getThiefId()) {
            return head;
        } else {
            return getClosest(thief);
        }
    }

    public OrdinaryThief getClosest(OrdinaryThief thief) {
        int i, curThiefPosition, curThiefDistance, minDistance;
        OrdinaryThief closestThief;

        closestThief = null;
        curThiefPosition = thief.getPosition();
        for (i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (thief.getThiefId() != data[i].getThiefId())
            {
                if (closestThief == null) {
                    closestThief = data[i];
                } else{
                    curThiefDistance = Math.abs(curThiefPosition - data[i].getPosition());
                    minDistance = Math.abs(curThiefPosition - closestThief.getPosition());
                    if (curThiefDistance == minDistance) {
                        if (thief.getPosition() > data[i].getPosition()) {
                            closestThief = data[i];
                        }
                    }
                    else if (curThiefDistance < minDistance) {
                        closestThief = data[i];
                    }
                } 
            }
        }
        System.out.println("[PARTYMEM] Party MEM determined that the closest theif to OT" + thief.getThiefId() + "(pos=" + thief.getPosition() + ") is OT" + closestThief.getThiefId() + "(pos =" + closestThief.getPosition() + ")" );
        return closestThief;
    }

    public boolean canMove(OrdinaryThief thief) {
        if (tail.getThiefId() == thief.getThiefId()) {
            return true;
        }
        return ( Math.abs(thief.getPosition() - getClosest(thief).getPosition()) < HeistConstants.MAX_CRAWLING_DISTANCE );
    }

    public void bestMove(OrdinaryThief thief) {
        int finalPosition, increment, minDistance;
        OrdinaryThief newTail;

        if (tail.getThiefId() == thief.getThiefId()) {
            increment =  Math.min(thief.getMaxDisplacement(), HeistConstants.MAX_CRAWLING_DISTANCE);
        } else 
        {
            increment = Math.min(thief.getMaxDisplacement(), HeistConstants.MAX_CRAWLING_DISTANCE - (Math.abs(thief.getPosition() - getClosest(thief).getPosition())));
        }
        finalPosition = thief.move(increment);

        if (finalPosition > head.getPosition()) {
            head = thief;
        }

        if (thief.getThiefId() == tail.getThiefId()) {
            newTail = tail;
            minDistance = tail.getPosition();
            for (int i = 0 ; i < HeistConstants.PARTY_SIZE; i++) {
                if (thief.getThiefId() != data[i].getThiefId())
                {
                    if (data[i].getPosition() < minDistance) {
                        minDistance = data[i].getPosition();
                        newTail = data[i];
                    }
                }
            }
            tail = newTail;
            System.out.println("[TAIL] Determined the new tail : OT" + tail.getThiefId() + "(pos=" + tail.getPosition() + ")");
        }
    }
}
