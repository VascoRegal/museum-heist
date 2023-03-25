package shared;

import structs.Semaphore;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import entities.Party;
import entities.Room;
import entities.ThiefState;

/**
 *  GeneralMemory class
 *
 *  Contains information regarding the state of each
 *  entity
 *
 *  Public methods are controlled with an access semaphore
 *
 */
public class GeneralMemory {
    
    /**
     *   Semaphore to ensure mutual exlusion
     */

    private final Semaphore access;

    /**
     *   is heist still running
     */

    private boolean heistInProgress;

    /**
     *   Reference to OTs Threads
     */

    private OrdinaryThief[] ordinaryThief;

    /**
     *   Reference to MT Thread
     */

    private MasterThief masterThief;

    /**
     *   Reference to rooms
     */

    private Room[] rooms;

    /**
     *   Reference to parties
     */

    private Party[] parties;

    /**
     *   Number of total paintings collected
     */

    private int totalPaintings;


    /**
     *  General Site memory instantiation.
     */

    public GeneralMemory() {
        heistInProgress = true;
        totalPaintings = 0;
        access = new Semaphore();
        access.up();
    }


    /**
     *   Set Ordinary THief state
     * 
     *      @param id thief id
     *      @param state thief state
     */

    public void setOrdinaryThiefState(int id, ThiefState state) {
        access.down();
        ordinaryThief[id].setThiefState(state);
        access.up();
    }

    /**
     *   Set Master Thief state
     * 
     *      @param state thief state
     */

    public void setMasterThiefState(ThiefState state) {
        access.down();
        masterThief.setThiefState(state);
        access.up();
    }


    /**
     *   Return heist statue
     * 
     *      @return true, if heist in progress
     *              false, if otherwise
     */

    public boolean isHeistInProgres() {
        boolean res;
        access.down();
        res = heistInProgress;
        access.up();
        return res;
    }

    /**
     *   Update reference
     */

    public void setOrdinaryThieves(OrdinaryThief[] ordinaryThiefs) {
        access.down();
        this.ordinaryThief = ordinaryThiefs;
        access.up();
    }

    /**
     *   Update reference
     */

    public void setMasterThief(MasterThief mt) {
        access.down();
        this.masterThief = mt;
        access.up();
    }

    /**
     *   Update reference
     */

    public void setParties(Party[] parties) {
        access.down();
        this.parties = parties;
        access.up();
    }

    /**
     *   Update reference
     */

    public void setRooms(Room[] rooms) {
        access.down();
        this.rooms = rooms;
        access.up();
    }

    /**
     *   Change heist state
     * 
     *      @param totalPaintings number of paints collected
     */

    public void finishHeist(int totalPaintings) {
        access.down();
        this.heistInProgress = false;
        this.totalPaintings = totalPaintings;
        access.up();
    }

    /**
     *   Log the state of the stored references
     */

    public void logInternalState() {
        access.down();
        String log = String.format("""
                    Heist to the Museum - Description of the internal state
        
            MstT            Thief 0         Thief 1         Thief 2         Thief 3         Thief 4         Thief 5
        """);

        log += String.format("    %2s          ", masterThief.getThiefState().label);

        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++ ) {
            if (ordinaryThief[i] != null) {
                log += String.format(" %2s  %2s  %2d", ordinaryThief[i].getThiefState().label, (ordinaryThief[i].getPartyId() == -1) ? "W" : "P", ordinaryThief[i].getMaxDisplacement());
                log += "    ";
            } else {
                log += String.format(" %2s  %2s  %2s", "X", "X", "X");
                
            }
        }

        log += "\n";
        log += String.format("""
                            Assault Party 0                         Assault Party 1                                         Museum
                    Elem1       Elem2       Elem3               Elem1        Elem2      Elem3               Room0     Room1     Room2     Room3     Room4 
        """);

        OrdinaryThief [] partyMembers;
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            if (parties[i] == null) {
                
                log += String.format("     %2s   %2s %2s %2s    %2s %2s %2s    %2s %2s %2s  ","X","X","X","X","X","X","X","X","X","X");
            } else {
                log += String.format("     %2d", parties[i].getRoomId());
                partyMembers = parties[i].memebersAsArray();
                
                for (int j = 0; j < partyMembers.length; j++ ) {
                    if (partyMembers[j] != null) {
                        log += String.format("  %2d %2d %2d  ", partyMembers[j].getThiefId(), partyMembers[j].getPosition(), (ordinaryThief[i].hasCanvas()) ? 1 : 0);
                    } else {
                        log += String.format("  %2s %2s %2s  ", "X","X","X");
                    }
                }
            }
        }
        log += "           ";
        for (int i = 0; i < HeistConstants.NUM_ROOMS; i++) {
            log += String.format("  %d %d   ", rooms[i].getNumHangingPaintings(), rooms[i].getLocation());
        }

        log += "\n";

        if (!heistInProgress) {
            log += String.format("\nMy friends, tonight's efforts produced %d priceless paintings.", this.totalPaintings);
        }

        log += "\n\n -------------------------------------------------------------------------------------------------- \n";

        System.out.println(log);
        access.up();
    }
}
