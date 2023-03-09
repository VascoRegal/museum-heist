package entities;

import consts.HeistConstants;
import structs.Utils;

public class Room {
    
    private final int id;

    private int numPaintings;

    private final int distance; 

    private RoomState state;

    public Room(int id) {
        this.id = id;
        numPaintings = Utils.randIntInRange(HeistConstants.MIN_NUM_PAINTINGS, HeistConstants.MAX_NUM_PAINTINGS);
        distance = Utils.randIntInRange(HeistConstants.MIN_DISTANCE_OUTSIDE, HeistConstants.MAX_DISTANCE_OUTSIDE);
        state = RoomState.AVAILABLE;
    }

    public void setRoomState(RoomState state) {
        this.state = state;
    }

    public RoomState getRoomState() {
        return this.state;
    }

    public void removePainting() {
        numPaintings--;
    }

    public boolean isEmpty() {
        return numPaintings == 0;
    }

    public int getId() {
        return id;
    }
}
