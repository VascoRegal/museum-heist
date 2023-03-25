package entities;

/**
 *  Room state for the MasterThief to track
 *  target rooms
 */

public enum RoomState {
    AVAILABLE,      // Can send a party
    IN_PROGRESS,    // There's already a party targeting this room
    COMPLETED       // Room cleared
}
