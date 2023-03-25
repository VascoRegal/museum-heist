package shared;

import consts.HeistConstants;
import entities.OrdinaryThief;
import entities.Room;
import entities.RoomState;
import structs.Semaphore;

public class MusuemMemory {
    
    private final GeneralMemory generalMemory;

    private final Semaphore access;

    private final Room [] rooms;

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

    public Room findNonClearedRoom() {
        for (int i=0 ; i < rooms.length; i++) {
            if (rooms[i].getRoomState() == RoomState.AVAILABLE) {
                return rooms[i];
            }
        }
        return null;
    }

    public void setRoomState(int roomId, RoomState roomState) {
        access.down();
        rooms[roomId].setRoomState(roomState);
        access.up();
    }

    public int getRoomLocation(int roomId) {
        return this.rooms[roomId].getLocation();
    }

    public void rollACanvas(int roomId) {
        OrdinaryThief currentThief;
        Room targetRoom;

        access.down();
        generalMemory.logInternalState();
        currentThief = ((OrdinaryThief) Thread.currentThread());
        targetRoom = rooms[roomId];

        if (!targetRoom.isEmpty()) {
            targetRoom.removePainting();
            currentThief.handleCanvas();
        }
        access.up();
    }

    public void markRoomAs(int roomId, RoomState state) {
        access.down();
        rooms[roomId].setRoomState(state);
        access.up();
    }    
}
