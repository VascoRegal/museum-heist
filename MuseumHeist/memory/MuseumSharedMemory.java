package MuseumHeist.memory;

public class MuseumSharedMemory<T> extends SharedMemoryRegion<T> {

    public MuseumSharedMemory(T structure) {
        super(structure);
    }
}
