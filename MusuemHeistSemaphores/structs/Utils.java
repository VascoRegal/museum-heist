package structs;

/**
 *  Utils class
 * 
 *  static class holding utility methods
 */
public final class Utils {

    /**
     * Generate a number in range.
     * Used for Thieves and Room attributes
     * 
     * @param min
     * @param max
     * @return int in range
     */

    public static int randIntInRange(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
    }

}
