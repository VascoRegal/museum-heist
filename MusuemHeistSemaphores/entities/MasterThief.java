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

        char action;
        int partyId;

        collectionSiteMemory.startOperations();
        //TODO: Assault ends condition
        while (true) {
            action = collectionSiteMemory.appraiseSit();
            System.out.println("ACTION : " + action);
            switch (action) {
                case 'p':
                    partyId = collectionSiteMemory.prepareAssaultParty();
                    collectionSiteMemory.sendAssaultParty(partyId);
                    break;
                case 'r':
                    collectionSiteMemory.takeARest();
                    collectionSiteMemory.collectACanvas();
                    break;
            }
        }
    }
}
