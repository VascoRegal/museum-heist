package MuseumHeist.memory;

public class GeneralSharedMemory<T> extends SharedMemoryRegion<T> {
    public GeneralSharedMemory(T structure) {
        super(structure);
    }
}
