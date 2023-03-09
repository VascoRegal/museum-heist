import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import shared.CollectionSiteMemory;
import shared.ConcentrationSiteMemory;
import shared.GeneralMemory;

public class MusuemHeist {

    protected static final Logger LOGGER = Logger.getLogger( Class.class.getName() );
    
    public static void main(String [] args) {
        
        OrdinaryThief [] ordinaryThieves = new OrdinaryThief [HeistConstants.NUM_THIEVES - 1];
        MasterThief masterThief = null;

        GeneralMemory generalMemory = new GeneralMemory();

        ConcentrationSiteMemory concentrationSite = new ConcentrationSiteMemory(generalMemory);
        CollectionSiteMemory collectionSite = new CollectionSiteMemory(generalMemory, concentrationSite);
        concentrationSite.setCollectionSiteMemory(collectionSite);

        for (int i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            ordinaryThieves[i] = new OrdinaryThief(i, concentrationSite);
        }
        masterThief = new MasterThief(0, collectionSite);

        masterThief.start();
        LOGGER.info("Started Master Thief");
        for (int i = 0; i < HeistConstants.NUM_THIEVES - 1; i++) {
            ordinaryThieves[i].start();
            LOGGER.info("Started Ordinary Thief " + i);
        }

    }
}
