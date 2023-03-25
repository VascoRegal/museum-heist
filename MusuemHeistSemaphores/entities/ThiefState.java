package entities;

/**
 *  Enum containing both thief type states
 */
public enum ThiefState {
    // MASTER THIEF
    PLANNING_THE_HEIST("PLN"),
    DECIDING_WHAT_TO_DO("DWD"),
    ASSEMBLING_A_GROUP("ASG"),
    WAITING_FOR_GROUP_ARRIVAL("WFG"),
    PRESENTING_THE_REPORT("REP"),

    // ORDINARY THIEF
    CONCENTRATION_SITE("CON"),
    CRAWLING_INWARDS("CIN"),
    AT_A_ROOM("ROM"),
    CRAWLING_OUTWARDS("COT"),
    COLLECTION_SITE("COL");


    /**
     *  Labels used in logging 
     */
    
    public final String label;

    private ThiefState(String lbl) {
        this.label = lbl;
    }
}
