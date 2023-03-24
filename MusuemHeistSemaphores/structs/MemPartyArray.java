package structs;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.ThiefState;

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

    public OrdinaryThief getNext() {
        OrdinaryThief currentThief;

        currentThief = getCurrentThief();

        if (currentThief.getThiefId() == tail.getThiefId()) {
            return head;
        } else {
            return getClosest();
        }
    }

    public OrdinaryThief getClosest() {
        int i, curThiefPosition, curThiefDistance, minDistance;
        OrdinaryThief currentThief, closestThief;

        currentThief = getCurrentThief();
        closestThief = null;
        curThiefPosition = currentThief.getPosition();
        for (i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (data[i].getThiefState() != currentThief.getThiefState() || currentThief.getThiefId() == data[i].getThiefId()) {
                continue;
            }

            if (closestThief == null) {
                closestThief = data[i];
            } else{
                curThiefDistance = Math.abs(curThiefPosition - data[i].getPosition());
                minDistance = Math.abs(curThiefPosition - closestThief.getPosition());
                if (curThiefDistance == minDistance) {
                    if (currentThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {
                        if (currentThief.getPosition() > data[i].getPosition()) {
                            closestThief = data[i];
                        }
                    } else if (currentThief.getPosition() < data[i].getPosition()) {
                        closestThief = data[i];
                    }
                }
                else if (curThiefDistance < minDistance) {
                    closestThief = data[i];
                }
            } 
            
        }
        return closestThief;
    }

    public boolean canMove() {
        OrdinaryThief currentThief, closestThief;

        currentThief = getCurrentThief();
        if (tail.getThiefId() == currentThief.getThiefId()) {
            return true;
        }
        closestThief = getClosest();
        return ( Math.abs(currentThief.getPosition() - closestThief.getPosition()) < HeistConstants.MAX_CRAWLING_DISTANCE );
    }

    public void doBestMove() {
        int finalPosition, increment, minDistance;
        OrdinaryThief newTail, currentThief, closestThief;

        currentThief = getCurrentThief();
        closestThief = getClosest();
        if (closestThief == null) {
            increment = currentThief.getMaxDisplacement();
            if (currentThief.getThiefState() == ThiefState.CRAWLING_OUTWARDS) {
                increment = - increment;
            }

            currentThief.move(increment);
            tail = currentThief;
            return;
        }

        if (tail.getThiefId() == currentThief.getThiefId()) {
            increment =  Math.min(currentThief.getMaxDisplacement(), HeistConstants.MAX_CRAWLING_DISTANCE);
        } else 
        {
            increment = Math.min(currentThief.getMaxDisplacement(), HeistConstants.MAX_CRAWLING_DISTANCE - (Math.abs(currentThief.getPosition() - closestThief.getPosition())));
        }

        if (currentThief.getThiefState() == ThiefState.CRAWLING_OUTWARDS) {
            increment = - increment;
        }

        finalPosition = currentThief.move(increment);

        if (currentThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {
            if (finalPosition > head.getPosition()) {
                head = currentThief;
            }
        } else {
            if (finalPosition < head.getPosition()) {
                head = currentThief;
            }
        }


        if (currentThief.getThiefId() == tail.getThiefId()) {
            newTail = tail;
            minDistance = tail.getPosition();
            for (int i = 0 ; i < HeistConstants.PARTY_SIZE; i++) {
                if (currentThief.getThiefId() != data[i].getThiefId())
                {
                    if (currentThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {
                        if (data[i].getPosition() < minDistance) {
                            minDistance = data[i].getPosition();
                            newTail = data[i];
                        }
                    } else {
                        if (data[i].getPosition() > minDistance) {
                            minDistance = data[i].getPosition();
                            newTail = data[i];
                        }
                    }
                }
            }
            tail = newTail;
        }
    }

    private OrdinaryThief getCurrentThief() {
        return ((OrdinaryThief) Thread.currentThread());
    }

    public OrdinaryThief head() {
        return this.head;
    }

    public OrdinaryThief tail() {
        return this.tail;
    }

    public OrdinaryThief[] asArray() {
        return data;
    }

    
}
