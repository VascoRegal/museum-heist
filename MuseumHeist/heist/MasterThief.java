package MuseumHeist.heist;

import MuseumHeist.HeistConstants;
import MuseumHeist.enums.ThiefState;
import MuseumHeist.memory.SharedMemoryManager;

public class MasterThief extends Thief {

    public MasterThief(String id, SharedMemoryManager sharedMemoryManager) {
        super(id, sharedMemoryManager);
        this.state = ThiefState.PLANNING_THE_HEIST;

        LOGGER.info(String.format("Created MasterThief with id=%s", id));
    }

    public void run() {
        LOGGER.info(String.format("Master Thief %s JOINED", id));
        while (true) {
            switch(this.state) {
                case PLANNING_THE_HEIST:
                    this.sharedMemoryManager.getHeistStatus().lock();
                    if (this.sharedMemoryManager.getHeistStatus().access().getNumRunningThieves() == HeistConstants.NUM_THIEVES - 1) {
                        LOGGER.info("All Thieves have joined.");
                        startOperations();
                    }
                    this.sharedMemoryManager.getHeistStatus().unlock();

                default:
                    break;
            }

        }
    }

    public void startOperations() {
        LOGGER.info("Starting Operations...");
        this.state = ThiefState.DECIDING_WHAT_TO_DO;
    }

}
