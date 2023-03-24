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

    private final ThiefState [] ordinaryThiefState;

    private ThiefState masterThiefState;

    private boolean heistInProgress;

    private OrdinaryThief[] ordinaryThief;

    private MasterThief masterThief;

    private Room[] rooms;

    private Party[] parties;


    public GeneralMemory() {
        ordinaryThiefState = new ThiefState [HeistConstants.NUM_THIEVES];
        for (int i=0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThiefState[i] = ThiefState.CONCENTRATION_SITE;
        }
        masterThiefState = ThiefState.PLANNING_THE_HEIST;
        heistInProgress = false;
        access = new Semaphore();
        heistInProgress = true;
        access.up();
    }

    public void setOrdinaryThiefState(int id, ThiefState state) {
        access.down();
        ordinaryThief[id].setThiefState(state);;
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

    public void finishHeist() {
        access.down();
        heistInProgress = false;
        access.up();
    }

    public void logInternalState() {
        String log = String.format("""
                    Heist to the Museum - Description of the internal state
        
            MstT            Thief 0         Thief 1         Thief 2         Thief 3         Thief 4         Thief 5
        """);

        log += String.format("     %s         ", masterThief.getThiefState().label);

        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++ ) {
            log += String.format(" %s  %s   %d ", ordinaryThief[i].getThiefState().label, (ordinaryThief[i].getPartyId() == -1) ? "W" : "P", ordinaryThief[i].getMaxDisplacement());
            log += "    ";
        }

        log += "\n";
        log += String.format("""
                    Assault Party 0                      Assault Party 1                           Museum
                Elem1    Elem2    Elem3             Elem1    Elem2    Elem3         Room0     Room1     Room2     Room3     Room4 
        """);

        OrdinaryThief [] partyMembers;
        for (int i = 0; i < HeistConstants.MAX_NUM_PARTIES; i++) {
            if (parties[i] == null) {
                log += "     X  X X X    X X X    X X X  ";
                log += "   ";
            } else {
                log += String.format("    %d ", parties[i].getRoomId());
                partyMembers = parties[i].memebersAsArray();
                
                for (int j = 0; j < partyMembers.length; j++ ) {
                    log += String.format("  %d %d %d  ", partyMembers[j].getThiefId(), partyMembers[j].getPosition(), (ordinaryThief[i].hasCanvas()) ? 1 : 0);
                }
                log += "  ";
            }
        }
        log += "   ";
        for (int i = 0; i < HeistConstants.NUM_ROOMS; i++) {
            log += String.format("  %d %d    ", rooms[i].getNumHangingPaintings(), rooms[i].getLocation());
        }

        log += "\n";

        if (!heistInProgress) {
            log += "\nMy friends, tonight's efforts produced %d priceless paintings.";
        }

        log += "\n\n -------------------------------------------------------------------------------------------------- \n";

        System.out.println(log);
    }
}
