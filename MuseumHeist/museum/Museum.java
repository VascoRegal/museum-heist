package MuseumHeist.museum;

import MuseumHeist.HeistConstants;

public class Museum {

    private Room[] rooms;
    private int sharedValue = 0;

    public Museum() {
        this.rooms = new Room[HeistConstants.NUM_ROOMS];
        for (int i =0; i < HeistConstants.NUM_ROOMS; i++) {
            this.rooms[i] = new Room();
        }
    }

    public Room getRoom(int id) {
        return this.rooms[id];
    }

    public void inc() {
        sharedValue += 1;
    }

    public int getVal() {
        return sharedValue;
    }
}
