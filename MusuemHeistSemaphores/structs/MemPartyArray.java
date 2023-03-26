package structs;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.ThiefState;

/**
 *  MemPartyArray
 *  
 *  Memory structure to manage the positions and
 *  movements of thieves in a party
 * 
 *  Has pointers to the logical head and tail of the
 *  movement
 */
public class MemPartyArray {
    
    /**
     *  Ordinary Thief in front of the line
     */
    private OrdinaryThief head;

    /**
     *  Ordinary Thief at back
     */
    private OrdinaryThief tail;

    /**
     *  Array with Ordinary Thief references
     */
    private OrdinaryThief [] data;


    /**
     *  Init
     * 
     *      @param data array to stor sthieves
     */
    public MemPartyArray(OrdinaryThief[] data) {
        this.data = data;
    }

    /**
     *  Add a thief to the structure
     * 
     *  Called by the Master Thief when forming parties
     * 
     *      @param thief object to add
     *      @throws MemException when party is full
     */
    public void join(OrdinaryThief thief) throws MemException {
        int insertIdx;

        if (head == null) {         // if head is undefinied, this is the first thief
            head = thief;           // assign it to head
            data[0] = thief;        // add it to the array
            return;
        }

        insertIdx = -1;         
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (data[i] == null) {  // find next insertion position
                data[i] = thief;    // add it
                insertIdx = i;
                break;
            }
        }

        if (insertIdx == -1) {
            throw new MemException("Party Array is full");
        }

        if (insertIdx == (data.length - 1) && tail == null) {   // if its the last thief
            tail = thief;                                       // assign it to tail
        }
    }

    /**
     *  Find the thief to remove and set its index to null
     *  If thief is head, assign next to head
     * 
     *      @param thief
     */
    public void leave(OrdinaryThief thief) {
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (data[i] != null && (data[i].getThiefId() == thief.getThiefId())) {
                data[i] = null;

                if (head.getThiefId() == thief.getThiefId()) {
                    head = getNext();
                }
            }
        }
    }

    /**
     *  Get the current moving thief
     * 
     *      @return thief
     */
    private OrdinaryThief getCurrentThief() {
        return ((OrdinaryThief) Thread.currentThread());
    }

    /**
     *  Get the next logical thief that should move
     * 
     *      @return thief to wake up next
     */
    public OrdinaryThief getNext() {
        OrdinaryThief currentThief;

        currentThief = getCurrentThief();

        if (currentThief.getThiefId() == tail.getThiefId()) {   // if current is tail
            return head;                                        // next is head
        } else {
            return getClosest();                                // else get the closest thief to current
        }
    }

    /**
     *   Get the closest thief to current
     * 
     *   Compares position between current and other thieves
     *   in the array and gets the lowest absolute difference
     * 
     *   If minimums are equal, return the one behind
     * 
     *      @return closest theif to current
     */
    public OrdinaryThief getClosest() {
        int i, curThiefPosition, curThiefDistance, minDistance;
        OrdinaryThief currentThief, closestThief;

        currentThief = getCurrentThief();
        closestThief = null;
        curThiefPosition = currentThief.getPosition();
        for (i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            if (data[i] != null) {                                                       // ignore thieves no longer in party
                if ( data[i].getThiefState() != currentThief.getThiefState() ||          // ignore thieves with different states
                currentThief.getThiefId() == data[i].getThiefId())                       // and dont compare current with itself!
                {
                    continue;
                }

                if (closestThief == null) {                                                 // if closest is unedifined
                    closestThief = data[i];                                                 // iteration is the closest so far
                } else{
                    curThiefDistance = Math.abs(curThiefPosition - data[i].getPosition());  // distance between current and iteraation thief
                    minDistance = Math.abs(curThiefPosition - closestThief.getPosition());  // distance between current and closest
                    if (curThiefDistance == minDistance) {                                  // if distances are equal, return the one behind
                        if (currentThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {  // the concept "behind" depends on the direction of the movement
                            if (currentThief.getPosition() > data[i].getPosition()) {
                                closestThief = data[i];
                            }
                        } else if (currentThief.getPosition() < data[i].getPosition()) {    // condition if movement is outwards
                            closestThief = data[i];                                         // update the closest
                        }
                    }
                    else if (curThiefDistance < minDistance) {                              // if found a new minimum
                        closestThief = data[i];                                             // update the closest
                    }
                } 
            }
        }
        return closestThief;
    }

    /**
     *  Checks if current thief can move
     *  
     *      @return true, if there are possible movements
     *              false, if movements would violate rules
     */
    public boolean canMove() {
        OrdinaryThief currentThief, closestThief;
        int closestDistance;

        currentThief = getCurrentThief();
        closestThief = getClosest();
        if (tail.getThiefId() == currentThief.getThiefId() ||                               // if current is tail
            closestThief == null                                                            // or there's no closest thief (current is the only still moving)
        ) {                          
            return true;                                                                    // he can always move (no thief will be left behind)
        }

        closestDistance = Math.abs(currentThief.getPosition() - closestThief.getPosition());// distance to the closest thief
        return ( closestDistance < HeistConstants.MAX_CRAWLING_DISTANCE );                  // if closest distance less than max distance, ret true
    }

    /**
     *  Execute the best possible movement and update
     *  pointers to head and tail
     */
    public void doBestMove() {
        int finalPosition, increment, minDistance;
        OrdinaryThief newTail, currentThief, closestThief;

        currentThief = getCurrentThief();
        closestThief = getClosest();
        if (closestThief == null) {                                                // there is no closestThief, current is alone in the movement
            increment = currentThief.getMaxDisplacement();                         // so he can always move the max possible distance
            if (currentThief.getThiefState() == ThiefState.CRAWLING_OUTWARDS) {
                increment = - increment;                                           // for the outwards movement, increments are negative
            }

            currentThief.move(increment);
            tail = currentThief;                                                
            return;
        }

        if (tail.getThiefId() == currentThief.getThiefId()) {         // if current is tail                                    
            increment =  currentThief.getMaxDisplacement();           // no need to check if thieves are being left behind
        } else                                                        // else compare the distances to find maximum increment possible
        {
            increment = Math.min(currentThief.getMaxDisplacement(), HeistConstants.MAX_CRAWLING_DISTANCE - (Math.abs(currentThief.getPosition() - closestThief.getPosition())));
        }
        
        if (currentThief.getThiefState() == ThiefState.CRAWLING_OUTWARDS) {     // increment is negative on outwards movement
            increment = - increment;
        }

        finalPosition = currentThief.move(increment);                           // move the thief

        if (currentThief.getThiefState() == ThiefState.CRAWLING_INWARDS) {      // update head if position is new maximum
            if (finalPosition > head.getPosition()) {
                head = currentThief;
            }
        } else {
            if (finalPosition < head.getPosition()) {
                head = currentThief;
            }
        }


        if (currentThief.getThiefId() == tail.getThiefId()) {                   // update tail if there's a new minimum
            newTail = tail;
            minDistance = tail.getPosition();
            for (int i = 0 ; i < HeistConstants.PARTY_SIZE; i++) {
                if (data[i] != null && (currentThief.getThiefId() != data[i].getThiefId()))
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

    /**
     *  Return current head
     *      @return head
     */
    public OrdinaryThief head() {
        return this.head;
    }

    /**
     *  Return current tail
     *      @return tail
     */
    public OrdinaryThief tail() {
        return this.tail;
    }

    /**
     *  Get raw thieves data
     *  Used for logging
     *  
     *      @return ordinary thieves array
     */
    public OrdinaryThief[] asArray() {
        return data;
    }

    
}
