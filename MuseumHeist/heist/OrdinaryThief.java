package MuseumHeist.heist;

import MuseumHeist.HeistConstants;
import MuseumHeist.Utils;
import MuseumHeist.enums.ThiefState;
import MuseumHeist.memory.SharedMemoryManager;

public class OrdinaryThief extends Thief
{
    private int position;
    private int md;

    public OrdinaryThief(String id, SharedMemoryManager sharedMemoryManager) {
        super(id, sharedMemoryManager);
        this.position = 0;
        this.md = Utils.randIntInRange(HeistConstants.MIN_THIEF_MD, HeistConstants.MAX_THIEF_MD);
        this.state = ThiefState.COLLECTION_SITE;

        LOGGER.info(String.format("Created OrdinaryThief with id=%s", id));
    }

    public void run() { 
        LOGGER.info(String.format("OrdinaryThief id=%s JOINED", id));
        this.sharedMemoryManager.getHeistStatus().access().ordinaryThiefJoin();
    }
}
