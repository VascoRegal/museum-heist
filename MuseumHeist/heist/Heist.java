package MuseumHeist.heist;

import java.util.logging.Logger;

import MuseumHeist.HeistConstants;
import MuseumHeist.memory.SharedMemoryManager;
import MuseumHeist.memory.SharedMemoryRegion;
import MuseumHeist.museum.Museum;

public class Heist {

    private static final Logger LOGGER = Logger.getLogger( Class.class.getName() );

    private MasterThief masterThief;
    private OrdinaryThief[] ordinaryThiefs;
    private Museum museum;
    private SharedMemoryManager sharedMemoryManager;
    private HeistStatus heistStatus;


    public Heist()
    {
        int i;
        this.museum = new Museum();
        this.heistStatus = new HeistStatus();

        this.sharedMemoryManager = new SharedMemoryManager.SharedMemoryManagerBuilder()
                                    .addMuseumRegion(new SharedMemoryRegion<Museum>(this.museum))
                                    .addHeistStatusRegion(new SharedMemoryRegion<HeistStatus>(this.heistStatus))
                                    .build();

        this.masterThief = new MasterThief("MT", this.sharedMemoryManager);

        this.ordinaryThiefs = new OrdinaryThief[HeistConstants.NUM_THIEVES - 1];
        for (i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            this.ordinaryThiefs[i] = new OrdinaryThief("OT" + i, this.sharedMemoryManager);
        }

        LOGGER.info("Heist Instantiated.");
    }

    public void start() {
        LOGGER.info("Starting Heist...");

        Thread mt = new Thread(this.masterThief);
        mt.start();

        for (int i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            Thread t = new Thread(this.ordinaryThiefs[i]);
            t.start();
        }
    }
}
