package structs;

public final class Utils {

    private Utils()
    {
    }

    public static int randIntInRange(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
    }

}
