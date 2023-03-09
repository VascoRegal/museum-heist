package entities;

import consts.HeistConstants;
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

    private void startOperations() {
        LOGGER.info("Starting Operations...");
        this.state = ThiefState.DECIDING_WHAT_TO_DO;
    }

    private void prepareAssaultParty() {
        LOGGER.info("Preparing Assault Party...");
        this.state = ThiefState.ASSEMBLING_A_GROUP;
        /* 
        Party party = new Party();
        OrdinaryThief ot;

        for (int i = 0; i < HeistConstants.PARTY_SIZE; i++) {
            ot = this.sharedMemoryManager.getConcentrationStatus().access().getAvailableThief();
            if (ot != null) {
                party.assignThief(ot);
                synchronized (ot) {
                    ot.notify();
                }
                LOGGER.info("ASSIGNED " + ot.id + " TO PARTY");
            }
        }
        */
    }

    private void sendAssaultParty() {
        int confirmedThieves = 0;

        while (confirmedThieves != HeistConstants.PARTY_SIZE) {
            try {
                synchronized(this) {
                    LOGGER.info("NOTIFYING MT PARTY IS READY " + confirmedThieves);
                    this.wait();
                }
                confirmedThieves += 1;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        LOGGER.info("Sending Assault Party...");
        this.state = ThiefState.DECIDING_WHAT_TO_DO;
    }

    private void takeARest() {
        LOGGER.info("Taking a Rest...");
        this.state = ThiefState.WAITING_FOR_GROUP_ARRIVAL;

        synchronized(this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void collectACanvas() {
        LOGGER.info("Collecting Canvas...");
    }

    private void appraiseSit() {
        LOGGER.info("Waiting for thieves...");
        synchronized(this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
