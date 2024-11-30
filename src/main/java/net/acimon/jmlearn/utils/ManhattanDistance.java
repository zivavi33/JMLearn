package net.acimon.jmlearn.utils;

/**
 * Utility class for calculating Manhattan distance between two points.
 * <p>
 * The Manhattan distance is the sum of the absolute differences between corresponding coordinates of two points in n-dimensional space.
 * </p>
 * 
 * <p><b>Note:</b> This class is intended to be used in machine learning algorithms where Manhattan distance is a suitable metric.</p>
 */
public class ManhattanDistance {

    /**
     * Computes the Manhattan distance (L1 distance) between two points represented as arrays of doubles.
     * <p>
     * The Manhattan distance is the sum of the absolute differences between corresponding coordinates of two points.
     * </p>
     * 
     * @param x1 The first point in the form of an array of doubles.
     * @param x2 The second point in the form of an array of doubles.
     * @return The Manhattan distance between the two points.
     * @throws IllegalArgumentException if the points have different dimensions (length).
     */
    public static double calculate(double[] x1, double[] x2) {
        if (x1.length != x2.length) {
            throw new IllegalArgumentException("Points must have the same number of dimensions.");
        }

        double sum = 0.0;
        for (int i = 0; i < x1.length; i++) {
            sum += Math.abs(x1[i] - x2[i]);
        }
        return sum;
    }
}
