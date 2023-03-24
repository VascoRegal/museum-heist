package entities;

import consts.HeistConstants;
import structs.Utils;
import shared.CollectionSiteMemory;
import shared.ConcentrationSiteMemory;
import shared.MusuemMemory;
import shared.PartiesMemory;

public class OrdinaryThief extends Thief
{
    private int position;
    private int md;
    private boolean hasCanvas;
    private ThiefPartyState partyState;
    private int partyId;
    private ConcentrationSiteMemory concentrationSiteMemory;
    private PartiesMemory partiesMemory;
    private MusuemMemory musuemMemory;
    private CollectionSiteMemory collectionSiteMemory;

    public OrdinaryThief(
        int id, 
        ConcentrationSiteMemory concentrationSiteMemory, 
        PartiesMemory partiesMemory, 
        MusuemMemory musuemMemory,
        CollectionSiteMemory collectionSiteMemory
    )
    {
        super(id);
        this.position = 0;
        this.md = Utils.randIntInRange(HeistConstants.MIN_THIEF_MD, HeistConstants.MAX_THIEF_MD);
        this.state = ThiefState.CONCENTRATION_SITE;
        this.partyState = ThiefPartyState.AVAILABLE;
        this.partyId = -1;
        this.hasCanvas = false;
        this.concentrationSiteMemory = concentrationSiteMemory;
        this.musuemMemory = musuemMemory;
        this.partiesMemory = partiesMemory;
        this.collectionSiteMemory = collectionSiteMemory;
    }

    public void run() {
        while (concentrationSiteMemory.amINeeded()) {
            int room = -1;

            partyId = concentrationSiteMemory.prepareExcursion();
            room = partiesMemory.crawlingIn();
            musuemMemory.rollACanvas(room);
            partiesMemory.crawlingOut();
            collectionSiteMemory.handACanvas();
        }
    }

    public void setPartyState(ThiefPartyState thiefPartyState) {
        partyState = thiefPartyState;
    }

    public ThiefPartyState getPartyState() {
        return partyState;
    }

    public void setPartyId(int id) {
        this.partyId = id;
    }

    public int getPartyId() {
        return this.partyId;
    }

    public int getMaxDisplacement() {
        return this.md;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public void handleCanvas() {
        this.hasCanvas = ! this.hasCanvas;
    }

    public boolean hasCanvas() {
        return this.hasCanvas;
    }

    public int move(int increment) {
        this.position += increment;
        return this.position;
    }
}
