package shared;

import structs.Semaphore;

import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import entities.Party;
import entities.Room;
import entities.ThiefState;

public class GeneralMemory {

    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );    
    
    private final Semaphore access;

    private boolean heistInProgress;

    private OrdinaryThief[] ordinaryThief;

    private MasterThief masterThief;

    private Room[] rooms;

    private Party[] parties;

    private int totalPaintings;


    public GeneralMemory() {
        heistInProgress = true;
        totalPaintings = 0;
        access = new Semaphore();
        access.up();
    }

    public void setOrdinaryThiefState(int id, ThiefState state) {
        access.down();
        ordinaryThief[id].setThiefState(state);
        access.up();
    }

    public void setMasterThiefState(ThiefState state) {
        access.down();
        masterThief.setThiefState(state);
        access.up();
    }

    public boolean isHeistInProgres() {
        boolean res;
        access.down();
        res = heistInProgress;
        access.up();
        return res;
    }

    public void setOrdinaryThieves(OrdinaryThief[] ordinaryThiefs) {
        this.ordinaryThief = ordinaryThiefs;
    }

    public void setMasterThief(MasterThief mt) {
        this.masterThief = mt;
    }

    public void setParties(Party[] parties) {
        this.parties = parties;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public void finishHeist(int totalPaintings) {
        access.down();
        this.heistInProgress = false;
        this.totalPaintings = totalPaintings;
        access.up();
    }

    public void logInternalState() {
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
    }
}
