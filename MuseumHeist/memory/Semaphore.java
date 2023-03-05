package MuseumHeist.memory;

public class Semaphore
{
    public int val = 0,
                numBlockedThreads = 0;

    public synchronized void down()
    {
        if (val == 0) {
            numBlockedThreads += 1;
            try {
                wait();
            }
            catch (InterruptedException e) {

            }
        }
        else {
            val -= 1;
        }
    }

    public synchronized void up()
    {
        if (numBlockedThreads != 0) {
            numBlockedThreads -= 1;
            notify();
        }
        else {
            val += 1;
        }
    }

}
