import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import shared.CollectionSiteMemory;
import shared.ConcentrationSiteMemory;
import shared.GeneralMemory;
import shared.MusuemMemory;
import shared.PartiesMemory;

public class MusuemHeist {

    protected static final Logger LOGGER = Logger.getLogger( Class.class.getName() );
    
    public static void main(String [] args) {
        
        OrdinaryThief [] ordinaryThieves = new OrdinaryThief [HeistConstants.NUM_THIEVES];
        MasterThief masterThief = null;

        GeneralMemory generalMemory = new GeneralMemory();
        MusuemMemory musuemMemory = new MusuemMemory();
        PartiesMemory partiesMemory = new PartiesMemory(musuemMemory);
        ConcentrationSiteMemory concentrationSite = new ConcentrationSiteMemory(generalMemory, partiesMemory);
        CollectionSiteMemory collectionSite = new CollectionSiteMemory(generalMemory, concentrationSite, musuemMemory, partiesMemory);
        concentrationSite.setCollectionSiteMemory(collectionSite);

        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThieves[i] = new OrdinaryThief(i, concentrationSite, partiesMemory, musuemMemory, collectionSite);
        }
        masterThief = new MasterThief(0, collectionSite, generalMemory);

        masterThief.start();
        // LOGGER.info("Started Master Thief");
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThieves[i].start();
            // LOGGER.info("Started Ordinary Thief " + i);
        }

        try {
            masterThief.join();
            for (int i=0; i < HeistConstants.NUM_THIEVES; i++) {
                ordinaryThieves[i].join();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
