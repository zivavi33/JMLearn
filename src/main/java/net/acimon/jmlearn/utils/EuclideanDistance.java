package net.acimon.jmlearn.utils;

/**
 * Utility class for calculating Euclidean distance for machine learning models.
 */
public class EuclideanDistance {

    /**
     * Computes the Euclidean distance between two n-dimensional points represented as arrays of doubles.
     * <p>
     * The Euclidean distance is the square root of the sum of the squared differences
     * between corresponding coordinates of two points in n-dimensional space.
     * </p>
     * 
     * @param x1 The first point in the form of an array of doubles.
     * @param x2 The second point in the form of an array of doubles.
     * @return The Euclidean distance between the two points.
     * @throws IllegalArgumentException if the points have different dimensions (length).
     */
    public static double calculate(double[] x1, double[] x2) {
        if (x1.length != x2.length) {
            throw new IllegalArgumentException("Points must have the same number of dimensions.");
        }

        double sum = 0.0;
        for (int i = 0; i < x1.length; i++) {
            sum += Math.pow(x1[i] - x2[i], 2);
        }
        return Math.sqrt(sum);
    }
    public static double calculate(double[] x1, double[] x2, boolean squared) {
        if (x1.length != x2.length) {
            throw new IllegalArgumentException("Points must have the same number of dimensions.");
        }

        double sum = 0.0;
        for (int i = 0; i < x1.length; i++) {
            sum += Math.pow(x1[i] - x2[i], 2);
        }
        return squared ? sum : Math.sqrt(sum);
    }

}