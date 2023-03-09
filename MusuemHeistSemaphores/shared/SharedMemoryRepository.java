package shared;

public class SharedMemoryRepository {
    private final CollectionSiteMemory collectionSiteMemory;

    private final ConcentrationSiteMemory concentrationSiteMemory;

    private final GeneralMemory generalMemory;

    public SharedMemoryRepository(
        CollectionSiteMemory collectionSiteMemory,
        ConcentrationSiteMemory concentrationSiteMemory,
        GeneralMemory generalMemory
    ) {
        this.collectionSiteMemory = collectionSiteMemory;
        this.concentrationSiteMemory = concentrationSiteMemory;
        this.generalMemory = generalMemory;
    }

    public CollectionSiteMemory getCollectionSiteMemory() {
        return this.collectionSiteMemory;
    }

    public ConcentrationSiteMemory getConcentrationSiteMemory() {
        return this.concentrationSiteMemory;
    }

    public GeneralMemory getGeneralMemory() {
        return this.generalMemory;
    }
}
