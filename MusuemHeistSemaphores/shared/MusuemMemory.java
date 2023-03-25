package shared;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Room;
import entities.RoomState;
import structs.Semaphore;


/**
 *  MuseumMemory class
 *
 *  Shared memory containing rooms and room operations
 *
 *  Public methods are controlled with an access semaphore
 */
public class MusuemMemory {
    
    /**
     *   Reference to the General Memory
     */

    private final GeneralMemory generalMemory;

    /**
     *   Semaphore to ensure mutual exlusion
     */

    private final Semaphore access;

     /*
     *   Array of rooms
     */

    private final Room [] rooms;

    /**
     *  Museum Memory memory instantiation.
     *
     *    @param generalMemory general memory reference
     */

    public MusuemMemory(GeneralMemory generalMemory) {
        this.generalMemory = generalMemory;
        access = new Semaphore();
        access.up();
        rooms = new Room [HeistConstants.NUM_ROOMS];
        for (int i=0; i < HeistConstants.NUM_ROOMS; i++) {
            rooms[i] = new Room(i);
        }
        this.generalMemory.setRooms(rooms);
    }

    /**
     *  Get a room available to send a party
     * 
     *  Called by the Master Thief when assembling a group
     *
     *    @return AVAILABLE room
     */

    public Room findNonClearedRoom() {
        access.down();
        for (int i=0 ; i < rooms.length; i++) {
            if (rooms[i].getRoomState() == RoomState.AVAILABLE) {
                access.up();
                return rooms[i];
            }
        }
        access.up();
        return null;
    }


    /**
     *  Update a room's state
     *
     *    @param roomId room identification
     *    @param roomState room state
     */
    public void setRoomState(int roomId, RoomState roomState) {
        access.down();
        rooms[roomId].setRoomState(roomState);
        access.up();
    }

    /**
     *  Get location of the room
     *  
     *  Called by Ordinary Thieves during movement
     * 
     *    @param roomId room identification
     *    @return room location
     */
    public int getRoomLocation(int roomId) {
        access.down();
        int loc = this.rooms[roomId].getLocation();
        access.up();
        return loc;
    }

    /**
     *  Update a room's state
     * 
     *  Roll a canvas. Called by Ordinary Thieves
     * 
     *  Nothing happens if there are no canvas on the walls
     *
     *    @param roomId room identification
     */
    public void rollACanvas(int roomId) {
        OrdinaryThief currentThief;
        Room targetRoom;

        access.down();
        // generalMemorylogInternalState();
        currentThief = ((OrdinaryThief) Thread.currentThread());
        targetRoom = rooms[roomId];

        if (!targetRoom.isEmpty()) {
            targetRoom.removePainting();
            currentThief.handleCanvas();
        }
        access.up();
    }

    /**
     *  MasterThief operation on a room's state
     *
     *    @param roomId room identification
     *    @param roomState room state
     */
    public void markRoomAs(int roomId, RoomState state) {
        access.down();
        rooms[roomId].setRoomState(state);
        access.up();
    }    
}
