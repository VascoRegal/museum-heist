import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import consts.HeistConstants;
import entities.MasterThief;
import entities.OrdinaryThief;
import shared.CollectionSiteMemory;
import shared.ConcentrationSiteMemory;
import shared.GeneralMemory;
import shared.MuseumMemory;
import shared.PartiesMemory;

public class MuseumHeist {

    protected static final Logger LOGGER = Logger.getLogger( Class.class.getName() );
    
    public static void main(String [] args) {
        
        if (args.length != 1) {
            usage();
            System.exit(1);
        } 

        try {
            setupLogging(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            usage();
            System.out.println("Could not create logging file");
            System.exit(1);
        }

        OrdinaryThief [] ordinaryThieves = new OrdinaryThief [HeistConstants.NUM_THIEVES];
        MasterThief masterThief = null;

        GeneralMemory generalMemory = new GeneralMemory();                      // init shared structures
        MuseumMemory museumMemory = new MuseumMemory(generalMemory);
        PartiesMemory partiesMemory = new PartiesMemory(museumMemory, generalMemory);
        ConcentrationSiteMemory concentrationSite = new ConcentrationSiteMemory(generalMemory, partiesMemory);
        CollectionSiteMemory collectionSite = new CollectionSiteMemory(generalMemory, concentrationSite, museumMemory, partiesMemory);
        concentrationSite.setCollectionSiteMemory(collectionSite);

        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThieves[i] = new OrdinaryThief(i, concentrationSite, partiesMemory, museumMemory, collectionSite);
        }
        generalMemory.setOrdinaryThieves(ordinaryThieves);
        masterThief = new MasterThief(0, collectionSite, generalMemory);
        generalMemory.setMasterThief(masterThief);

        masterThief.start();                                                // start master thief thread
        for (int i = 0; i < HeistConstants.NUM_THIEVES; i++) {
            ordinaryThieves[i].start();                                     // start oridnary thieves thread
        }

        try {           
            masterThief.join();                                             // wait for thread join
            for (int i=0; i < HeistConstants.NUM_THIEVES; i++) {
                ordinaryThieves[i].join();                  
            }
        } catch (InterruptedException e) {  
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void usage() {
        System.out.println("""
            [USAGE]
                java MuseumHeist [logFile] 

                    - logFile: path to logging file
                """);
    }

    public static void setupLogging(String logFile) throws IOException {
        File log;

        log = new File(logFile);
        log.createNewFile();
        PrintStream ps = new PrintStream(log);
        System.setOut(ps);
    }
}
