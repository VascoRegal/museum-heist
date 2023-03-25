package entities;

import shared.CollectionSiteMemory;
import shared.GeneralMemory;

/**
 *  MasterThief class
 * 
 */

public class MasterThief extends Thief {

    /**
     *   Reference to the Collection Site Memory
     */

    private final CollectionSiteMemory collectionSiteMemory;

    /**
     *   Reference to the General Memory
     */

    private final GeneralMemory generalMemory;

    /**
     *  Collection Site memory instantiation.
     *    
     *    @param id thief identification
     *    @param generalMemory general memory reference
     *    @param collectionSiteMemory collection site memory reference
     */

    public MasterThief(int id, CollectionSiteMemory collectionSiteMemory, GeneralMemory generalMemory) {
        super(id);
        this.state = ThiefState.PLANNING_THE_HEIST;
        this.collectionSiteMemory = collectionSiteMemory;
        this.generalMemory = generalMemory;
    }

    /**
     *  Main lifecycle
     * 
     */
    public void run() {

        char action;
        int partyId;

        collectionSiteMemory.startOperations();                             // start the operations
        while (generalMemory.isHeistInProgres()) {                          // while heist is running
            //generalMemory.logInternalState();
            action = collectionSiteMemory.appraiseSit();                    // decide what action to do next
            switch (action) {           
                case 'p':                                                   // 'p' - create a party
                    partyId = collectionSiteMemory.prepareAssaultParty();   // prepare it
                    collectionSiteMemory.sendAssaultParty(partyId);         // send it when ready
                    break;
                case 'r':                                                   // rest  
                    collectionSiteMemory.takeARest();                       // wait for thieves arrival
                    collectionSiteMemory.collectACanvas();                  // awake to collect canvas
                    break;
                case 's':
                    break;
            }
        }
        collectionSiteMemory.sumUpResults();                                // sum up and present the results
        //generalMemory.logInternalState();
    }
}
