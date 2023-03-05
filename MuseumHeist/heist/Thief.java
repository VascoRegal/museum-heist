package MuseumHeist.heist;

import java.util.logging.Logger;

import MuseumHeist.enums.ThiefState;
import MuseumHeist.memory.SharedMemoryManager;

public abstract class Thief implements Runnable {

    protected static final Logger LOGGER = Logger.getLogger( Class.class.getName() );

    protected SharedMemoryManager sharedMemoryManager;
    protected String id;
    protected ThiefState state;

    public Thief(String id, SharedMemoryManager sharedMemoryManager) {
        this.id = id;
        this.sharedMemoryManager = sharedMemoryManager;
        Thread.currentThread().setName(this.toString());
    }

    public void run()
    {
    }

    public String toString() {
        return "[ " + this.getClass().getName() + " - " + this.id + " ]";
    }
}
