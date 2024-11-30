package net.acimon.jmlearn.metrics;

/**
 * class for calculating accuracy in machine learning models.
 * <p>
 * This class provides the calculation of accuracy, which measures the proportion of correct predictions
 * compared to the total number of predictions.
 * </p>
 * 
 * <p><b>Note:</b> Accuracy is widely used for classification problems.</p>
 */
public class Accuracy {

    /**
     * Computes the accuracy between true labels and predicted labels.
     * <p>
     * The accuracy is calculated as the number of correct predictions divided by the total number of predictions.
     * </p>
     * 
     * @param yTrue The true labels (ground truth) in the form of an array of integers.
     * @param yPred The predicted labels in the form of an array of integers.
     * @return The accuracy as a double, ranging from 0.0 to 1.0.
     * @throws IllegalArgumentException if the arrays have different lengths.
     */
    public static double calculate(int[] yTrue, int[] yPred) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("The true and predicted label arrays must have the same length.");
        }

        int correct = 0;
        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == yPred[i]) {
                correct++;
            }
        }
        return (double) correct / yTrue.length;
    }
}
