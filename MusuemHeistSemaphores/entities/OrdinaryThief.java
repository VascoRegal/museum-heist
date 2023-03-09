package entities;

import consts.HeistConstants;
import structs.Utils;
import entities.ThiefState;
import shared.ConcentrationSiteMemory;

public class OrdinaryThief extends Thief
{
    private int position;
    private int md;
    private int hasCanvas;
    private boolean needed;
    private ConcentrationSiteMemory concentrationSiteMemory;

    public OrdinaryThief(int id, ConcentrationSiteMemory concentrationSiteMemory) {
        super(id);
        this.position = 0;
        this.md = Utils.randIntInRange(HeistConstants.MIN_THIEF_MD, HeistConstants.MAX_THIEF_MD);
        this.state = ThiefState.CONCENTRATION_SITE;
        this.hasCanvas = 0;
        this.needed = false;
        this.concentrationSiteMemory = concentrationSiteMemory;
    }

    public void run() {
        while (true) {
            if (concentrationSiteMemory.amINeeded()) {
                concentrationSiteMemory.prepareExcursion();
            }
        }
    }

    public int hasCanvas() {
        return this.hasCanvas;
    }

    private void rollACanvas() {
        if (this.hasCanvas == 1) {
            return;
        } else {
            this.hasCanvas = 1;
        }
    }

    private void handACanvas() {
        if (this.hasCanvas == 0) {
            return;
        } else {
            this.hasCanvas = 0;
        }
        this.state = ThiefState.COLLECTION_SITE;
    }

    private void amINeeded() {
        this.notifyMT();
    }

    private void prepareExcursion() {
        LOGGER.info("Ordinary Thief " + this.id + " ready for excursion.");
        this.notifyMT();
        this.state = ThiefState.CRAWLING_INWARDS;
    }

    private void notifyMT(){

    }
}
