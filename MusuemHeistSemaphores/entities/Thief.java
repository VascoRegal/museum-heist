package entities;

import java.util.logging.Logger;


public abstract class Thief extends Thread {

    protected static final Logger LOGGER = Logger.getLogger( Class.class.getName() );

    protected final int id;
    
    protected ThiefState state;

    public Thief(int id) {
        this.id = id;
        Thread.currentThread().setName(this.toString());
    }

    public void run()
    {
    }

    public int getThiefId() {
        return this.id;
    }

    public void setState(ThiefState state) {
        this.state = state;
    }

    public String toString() {
        return "[ " + this.getClass().getName() + " - " + this.id + " ]";
    }
}
