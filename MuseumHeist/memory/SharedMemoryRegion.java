package MuseumHeist.memory;

import java.util.concurrent.Semaphore;

public class SharedMemoryRegion<T> {
    private Semaphore sem;
    protected T memory;

    public SharedMemoryRegion(T structure)
    {
        this.sem = new Semaphore(1);
        this.memory = structure;
    }


    public void lock() {
        try {
            this.sem.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void unlock() {
        this.sem.release();
    }

    public T access() {
        return memory;
    }
}
