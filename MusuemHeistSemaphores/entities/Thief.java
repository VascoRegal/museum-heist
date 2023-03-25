package entities;

/**
 *  Thief class
 * 
 *  Abstract class implementing common operations between
 *  both thief types
 * 
 *  Thread subclass 
 * 
 */
public abstract class Thief extends Thread {

    /**
     *  Thief identification
     */

    protected final int id;
    
    /**
     *  Thief state
     */

    protected ThiefState state;

    /**
     *  Instantiation
     * 
     *      @param id thief identification
     */

    public Thief(int id) {
        this.id = id;
        Thread.currentThread().setName(this.toString());
    }

    /**
     *  Get Thief identification
     * 
     *      @return thief id
     */
    public int getThiefId() {
        return this.id;
    }

    /**
     *  Get Thief state
     * 
     *      @return thief state
     */
    public ThiefState getThiefState() {
        return state;
    }

    /**
     *  Set Thief state
     * 
     *      @param thief state
     */
    public void setThiefState(ThiefState state) {
        this.state = state;
    }
}
