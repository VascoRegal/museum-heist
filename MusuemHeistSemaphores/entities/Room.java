package entities;

import consts.HeistConstants;
import structs.Utils;

/**
 *  Room class
 * 
 *  Represents room in a museum
 * 
 */
public class Room {
    
    /**
     *  Room identification
     */

    private final int id;

    /**
     *  Number of hanging paintings
     */

    private int numPaintings;

    /**
     *  Distance to the site
     */

    private final int distance; 

    /**
     *  Room state
     */

    private RoomState state;

    /**
     *  Instantiation 
     * 
     *      @param id room id
     */
    public Room(int id) {
        this.id = id;
        numPaintings = Utils.randIntInRange(HeistConstants.MIN_NUM_PAINTINGS, HeistConstants.MAX_NUM_PAINTINGS);
        distance = Utils.randIntInRange(HeistConstants.MIN_DISTANCE_OUTSIDE, HeistConstants.MAX_DISTANCE_OUTSIDE);
        state = RoomState.AVAILABLE;
    }

    /**
     *  Set Room state
     * 
     *      @param state room state
     */

    public void setRoomState(RoomState state) {
        this.state = state;
    }

    /**
     *  Get Room state
     * 
     *      @return room state
     */

    public RoomState getRoomState() {
        return this.state;
    }

    /**
     *  Get Hanging paintings
     * 
     *      @return number of paitnings hanging
     */

    public int getNumHangingPaintings() {
        return numPaintings;
    }

    /**
     *  Remove a painting, called by Ordinary Thief
     * 
     */

    public void removePainting() {
        numPaintings--;
    }

    /**
     *  Check if room is empty
     * 
     *      @return true, if no paintings are up
     *              false, otherwise
     */

    public boolean isEmpty() {
        return numPaintings == 0;
    }

    /**
     *  Get Room id
     * 
     *      @return id room id
     */

    public int getId() {
        return id;
    }

    /**
     *  Get Room location
     * 
     *      @return distance outside
     */

    public int getLocation() {
        return this.distance;
    }
}
