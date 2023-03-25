package entities;

import shared.CollectionSiteMemory;
import shared.GeneralMemory;

public class MasterThief extends Thief {

    private final CollectionSiteMemory collectionSiteMemory;

    private final GeneralMemory generalMemory;

    public MasterThief(int id, CollectionSiteMemory collectionSiteMemory, GeneralMemory generalMemory) {
        super(id);
        this.state = ThiefState.PLANNING_THE_HEIST;
        this.collectionSiteMemory = collectionSiteMemory;
        this.generalMemory = generalMemory;
    }

    public void run() {

        char action;
        int partyId;

        collectionSiteMemory.startOperations();
        while (generalMemory.isHeistInProgres()) {
            action = collectionSiteMemory.appraiseSit();
            switch (action) {
                case 'p':
                    generalMemory.logInternalState();
                    partyId = collectionSiteMemory.prepareAssaultParty();
                    generalMemory.logInternalState();
                    collectionSiteMemory.sendAssaultParty(partyId);
                    generalMemory.logInternalState();
                    break;
                case 'r':
                    generalMemory.logInternalState();
                    collectionSiteMemory.takeARest();
                    generalMemory.logInternalState();
                    collectionSiteMemory.collectACanvas();
                    generalMemory.logInternalState();
                    break;
                case 's':
                    break;
            }
        }
        collectionSiteMemory.sumUpResults();
        generalMemory.logInternalState();
    }
}
