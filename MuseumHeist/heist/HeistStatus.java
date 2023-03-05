package MuseumHeist.heist;

public class HeistStatus {
    private int runningThieves;

    public HeistStatus() {
        this.runningThieves = 0;
    }

    public int getNumRunningThieves() {
        return this.runningThieves;
    }

    private void setNumRunningThieves(int n) {
        this.runningThieves = n;
    }

    public void ordinaryThiefJoin() {
        this.runningThieves += 1;
    }

    public void ordinaryThiefLeave() {
        this.runningThieves -= 1;
    }

}
