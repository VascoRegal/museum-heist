package MuseumHeist.memory;

import MuseumHeist.heist.HeistStatus;
import MuseumHeist.museum.Museum;

public class SharedMemoryManager {

    // required
    private SharedMemoryRegion<Museum> museum;
    private SharedMemoryRegion<HeistStatus> heist;

    public SharedMemoryRegion<Museum> getMuseum() {
        return this.museum;
    }

    public SharedMemoryRegion<HeistStatus> getHeistStatus() {
        return this.heist;
    }

    private SharedMemoryManager(SharedMemoryManagerBuilder builder) {
        this.museum = builder.museum;
        this.heist = builder.heist;
    }

    //Builder
    public static class SharedMemoryManagerBuilder {
        private SharedMemoryRegion<Museum> museum;
        private SharedMemoryRegion<HeistStatus> heist;  

        public SharedMemoryManagerBuilder() {}

        public SharedMemoryManagerBuilder addMuseumRegion(SharedMemoryRegion<Museum> museum) {
            this.museum = museum;
            return this;
        }

        public SharedMemoryManagerBuilder addHeistStatusRegion(SharedMemoryRegion<HeistStatus> heist) {
            this.heist = heist;
            return this;
        }

        public SharedMemoryManager build() {
            return new SharedMemoryManager(this);
        }
    }
}
