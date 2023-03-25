package entities;

import consts.HeistConstants;
import structs.MemPartyArray;

/**
 *  Party Class
 * 
 *  Represents an instance of a party
 * 
 *  Movement options called by Ordinary Thieves during movement
 */
public class Party {
    
    /**
     *  Party identification
     */

    private final int id;

    /**
     *  Target room identification
     */

    private final int roomId;

    /**
     *  MemPartyArray of party memebers
     */

    private final  MemPartyArray partyArray;

    /**
     *  Instantiation
     * 
     *      @param id party identification
     *      @param roomId   room identification
     */
    public Party(int id, int roomId) {
        this.id = id;
        this.roomId = roomId;

        OrdinaryThief[] thieves = new OrdinaryThief[HeistConstants.PARTY_SIZE];
        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++ ) {
            thieves[i] = null;
        }

        partyArray = new MemPartyArray(thieves);
    }

    /**
     *  Get party identification
     * 
     *      @return party id
     */

    public int getId() {
        return this.id;
    }

    /**
     *  Get first thief in line
     * 
     *      @return thief id
     */

    public int getFirst() {
        return partyArray.head().getThiefId();
    }

    /**
     *  Get last thief in line
     * 
     *      @return thief id
     */

    public int getLast() {
        return partyArray.tail().getThiefId();
    }

    /**
     *  Get target room identification
     * 
     *      @return room id
     */

    public int getRoomId() {
        return this.roomId;
    }

    /**
     *  Get the next thief that should move
     * 
     *      @return thief
     */

    public OrdinaryThief getNext() {
        return partyArray.getNext();
    }

    /**
     *  Add ordinary thief to party
     * 
     *      @param thief to add
     */

    public void join(OrdinaryThief ordinaryThief) {
        partyArray.join(ordinaryThief);
    }


    /**
     *  Check if thief can move
     * 
     *      @return true, if thief can move
     *              false, oterwise
     */

    public boolean canIMove() {
        return partyArray.canMove();
    }


    /**
     *  Execute best movement
     * 
     */

    public void move() {
        partyArray.doBestMove();
    }

    /**
     *  Get raw ordinary thief array
     *  Used for logging
     */

    public OrdinaryThief[] memebersAsArray() {
        return partyArray.asArray();
    }
}
