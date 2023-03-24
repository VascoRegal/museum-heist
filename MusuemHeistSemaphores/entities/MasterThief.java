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
                    partyId = collectionSiteMemory.prepareAssaultParty();
                    collectionSiteMemory.sendAssaultParty(partyId);
                    break;
                case 'r':
                    collectionSiteMemory.takeARest();
                    collectionSiteMemory.collectACanvas();
                    break;
                case 's':
                    break;
            }
        }
        collectionSiteMemory.sumUpResults();
    }
}
