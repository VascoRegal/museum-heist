package shared;

import structs.Semaphore;
import consts.HeistConstants;
import entities.ThiefState;

public class GeneralMemory {
    
    private final Semaphore access;

    private final ThiefState [] ordinaryThiefState;

    private ThiefState masterThiefState;

    private boolean heistInProgress;

    private int numActiveParties;

    public GeneralMemory() {
        ordinaryThiefState = new ThiefState [HeistConstants.NUM_THIEVES];
        for (int i=0; i < HeistConstants.NUM_THIEVES - 1; i++) {
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
        ordinaryThiefState[id] = state;
        access.up();
    }

    public void setMasterThiefState(ThiefState state) {
        access.down();
        masterThiefState = state;
        access.up();
    }

    public boolean isHeistInProgres() {
        return heistInProgress;
    }

    public int getNumParties() {
        return numActiveParties;
    }

    public void incrementParties() {
        numActiveParties++;
    }

    public void decrementParties() {
        numActiveParties--;
    }
}
