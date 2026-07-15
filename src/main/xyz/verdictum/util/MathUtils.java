// MathUtils.java
package verdictum.util;

public class MathUtils {
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double combineCrimes(double[] rValues) {
        if (rValues.length == 0) return 0;
        if (rValues.length == 1) return rValues[0];
        double i = 1 - rValues[0];
        for (int j = 1; j < rValues.length; j++) {
            double term = 1 - rValues[j];
            i = (i < 0) ? i * Math.abs(term) : i * term;
        }
        return 1 - i;
    }
}