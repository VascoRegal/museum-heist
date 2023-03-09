package entities;

import shared.CollectionSiteMemory;

public class MasterThief extends Thief {

    private final CollectionSiteMemory collectionSiteMemory;

    public MasterThief(int id, CollectionSiteMemory collectionSiteMemory) {
        super(id);
        this.state = ThiefState.PLANNING_THE_HEIST;
        this.collectionSiteMemory = collectionSiteMemory;
    }

    public void run() {
        collectionSiteMemory.startOperations();
        while (true) {
            collectionSiteMemory.prepareAssaultParty();
        }
    }
}
