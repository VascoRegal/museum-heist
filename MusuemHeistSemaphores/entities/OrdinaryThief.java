package entities;

import consts.HeistConstants;
import structs.Utils;
import shared.CollectionSiteMemory;
import shared.ConcentrationSiteMemory;
import shared.GeneralMemory;
import shared.MusuemMemory;
import shared.PartiesMemory;


/**
 *  Ordinary Thief class
 * 
 *  Represent an ordinary thief, containing information
 *  to handle states, canvas and movement
 * 
 */
public class OrdinaryThief extends Thief
{
    /**
     *  Position of the thief 
     */
    
    private int position;
    
    /**
     *  Thief maximum displacement
     */

    private int md;

    /**
     *  Thief canvas holding status
     */

    private boolean hasCanvas;

    /**
     *  Thief party identification
     */

    private int partyId;

     /**
     *   Reference to the General Memory
     */
 
     private final GeneralMemory generalMemory;
 
     /**
     *   Reference to the Concentration Site Memory
     */
 
     private final ConcentrationSiteMemory concentrationSiteMemory;
 
     /**
     *   Reference to the Musuem Memory
     */
 
     private final MusuemMemory musuemMemory;
 
     /**
     *   Reference to the Parties Memory
     */
 
     private final PartiesMemory partiesMemory;

    /**
     *   Reference to the Collection Site Memory
     */
 
     private final CollectionSiteMemory collectionSiteMemory;


    /**
     *  Collection Site memory instantiation.
     *
     *    @param generalMemory general memory reference
     *    @param concentrationSiteMemory concentration memory reference
     *    @param museumMemory museum memory reference
     *    @param partiesMemory parties memory reference
     *    @param collectionSiteMemory collection site memory reference
     */

    public OrdinaryThief(
        int id, 
        ConcentrationSiteMemory concentrationSiteMemory, 
        PartiesMemory partiesMemory, 
        MusuemMemory musuemMemory,
        CollectionSiteMemory collectionSiteMemory,
        GeneralMemory generalMemory
    )
    {
        super(id);
        this.position = 0;      // initial position
        this.md = Utils.randIntInRange(HeistConstants.MIN_THIEF_MD, HeistConstants.MAX_THIEF_MD);   // random number
        this.state = ThiefState.CONCENTRATION_SITE;     //initial state
        this.partyId = -1;
        this.hasCanvas = false;
        this.concentrationSiteMemory = concentrationSiteMemory;
        this.musuemMemory = musuemMemory;
        this.partiesMemory = partiesMemory;
        this.collectionSiteMemory = collectionSiteMemory;
        this.generalMemory = generalMemory;
    }

    /**
     * 
     *  Main lifecylce
     */
    public void run() {
        while (concentrationSiteMemory.amINeeded()) {               // while thief is needed
            int room = -1;                                          // reference to target room

            partyId = concentrationSiteMemory.prepareExcursion();   // if he's needed, prepare the excursion
            room = partiesMemory.crawlingIn();                      // begin crawling when ready                     
            musuemMemory.rollACanvas(room);                         // at the room, steal canvas
            partiesMemory.crawlingOut();                            // crawl back to site
            collectionSiteMemory.handACanvas();                     // hand the canvas
        }
    }

    /**
     *  Set Thief party identification
     * 
     *      @param id party id
     */

    public void setPartyId(int id) {
        this.partyId = id;
    }

    /**
     *  Get Thief party identification
     * 
     *      @return party id
     */

    public int getPartyId() {
        return this.partyId;
    }

    /**
     *  Get Thief max displace
     * 
     *      @return thief md
     */

    public int getMaxDisplacement() {
        return this.md;
    }

    /**
     *  Get Thief position
     * 
     *      @return thief position
     */

    public int getPosition() {
        return this.position;
    }

    /**
     *  Set Thief postion
     * 
     *      @param thief position
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     *  Toggle the canvas flag
     */

    public void handleCanvas() {
        this.hasCanvas = ! this.hasCanvas;
    }

    /**
     *  Get canvas state
     *  
     *      @return true if thief holding canvas
     *              false if not
     */

    public boolean hasCanvas() {
        return this.hasCanvas;
    }

    /**
     *  Move thief by an increment and return
     *  new position
     *      
     *      @param increment
     *      @return final position
     */

    public int move(int increment) {
        this.position += increment;
        return this.position;
    }
}
