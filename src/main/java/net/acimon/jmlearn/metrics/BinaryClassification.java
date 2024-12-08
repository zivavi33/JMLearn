package net.acimon.jmlearn.metrics;

public class BinaryClassification {

    private int tp; // True Positives
    private int tn; // True Negatives
    private int fp; // False Positives
    private int fn; // False Negatives

    /**
     * Constructor to compute the confusion matrix components.
     *
     * @param yTrue True labels (ground truth).
     * @param yPred Predicted labels.
     */
    public BinaryClassification(int[] yTrue, int[] yPred) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("The true and predicted label arrays must have the same length.");
        }
        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == 1 && yPred[i] == 1) tp++;
            else if (yTrue[i] == 0 && yPred[i] == 0) tn++;
            else if (yTrue[i] == 0 && yPred[i] == 1) fp++;
            else if (yTrue[i] == 1 && yPred[i] == 0) fn++;
        }
    }

    /**
     * Accuracy: (TP + TN) / Total
     */
    public double accuracy() {
        return (double) (tp + tn) / (tp + tn + fp + fn);
    }

    /**
     * Precision: TP / (TP + FP)
     */
    public double precision() {
        return tp + fp == 0 ? 0 : (double) tp / (tp + fp);
    }

    /**
     * Recall (Sensitivity): TP / (TP + FN)
     */
    public double recall() {
        return tp + fn == 0 ? 0 : (double) tp / (tp + fn);
    }

    /**
     * Specificity: TN / (TN + FP)
     */
    public double specificity() {
        return tn + fp == 0 ? 0 : (double) tn / (tn + fp);
    }

    /**
     * F1 Score: 2 * (Precision * Recall) / (Precision + Recall)
     */
    public double f1Score() {
        double precision = precision();
        double recall = recall();
        return precision + recall == 0 ? 0 : 2 * (precision * recall) / (precision + recall);
    }

    /**
     * Matthews Correlation Coefficient (MCC):
     * (TP * TN - FP * FN) / sqrt((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN))
     */
    public double mcc() {
        double numerator = (double) (tp * tn - fp * fn);
        double denominator = Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
        return denominator == 0 ? 0 : numerator / denominator;
    }

    /**
     * Confusion Matrix: Returns an array [TP, TN, FP, FN].
     */
    public int[] confusionMatrix() {
        return new int[]{tp, tn, fp, fn};
    }

    /**
     * Balanced Accuracy: Average of Sensitivity and Specificity.
     */
    public double balancedAccuracy() {
        return (recall() + specificity()) / 2.0;
    }

    /**
     * Misclassification Rate: (FP + FN) / Total
     */
    public double misclassificationRate() {
        return (double) (fp + fn) / (tp + tn + fp + fn);
    }

    /**
     * F-Beta Score: Generalized F1 Score with beta weight.
     * 
     * @param beta Weight of recall in the score.
     */
    public double fBetaScore(double beta) {
        double precision = precision();
        double recall = recall();
        double betaSquared = beta * beta;
        return precision + recall == 0 ? 0 : (1 + betaSquared) * (precision * recall) / (betaSquared * precision + recall);
    }

    /**
     * Outputs all metrics as a formatted string.
     */
    public String summary() {
        return String.format(
            "Accuracy: %.4f%n" +
            "Precision: %.4f%n" +
            "Recall (Sensitivity): %.4f%n" +
            "Specificity: %.4f%n" +
            "F1 Score: %.4f%n" +
            "MCC: %.4f%n" +
            "Balanced Accuracy: %.4f%n" +
            "Misclassification Rate: %.4f%n" +
            "Confusion Matrix: [TP=%d, TN=%d, FP=%d, FN=%d]%n",
            accuracy(), precision(), recall(), specificity(), f1Score(),
            mcc(), balancedAccuracy(), misclassificationRate(), tp, tn, fp, fn
        );
    }

}
