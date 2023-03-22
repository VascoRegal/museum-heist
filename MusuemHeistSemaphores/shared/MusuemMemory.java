package shared;

import consts.HeistConstants;
import entities.Room;
import entities.RoomState;
import structs.Semaphore;

public class MusuemMemory {
    
    private final Semaphore access;

    private final Room [] rooms;

    public MusuemMemory() {
        access = new Semaphore();
        access.up();
        rooms = new Room [HeistConstants.NUM_ROOMS];
        for (int i=0; i < HeistConstants.NUM_ROOMS; i++) {
            rooms[i] = new Room(i);
        }
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

    public void rollACanvas(int partyId) {
        System.out.println("we can roll it up");
    }

    
}
