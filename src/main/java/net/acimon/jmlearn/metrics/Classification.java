package net.acimon.jmlearn.metrics;

import java.util.Arrays;

/**
 * The Classification class provides methods to compute common classification metrics.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Calculate common classification metrics: Accuracy, Precision, Recall, F1 score, Specificity, and MCC.</li>
 *     <li>Compute confusion matrix for each class to analyze true positives (TP), true negatives (TN), false positives (FP), and false negatives (FN).</li>
 *     <li>Support for averaging strategies for multi-class problems: Macro, Micro, and Weighted averages.</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * int[] yTrue = {0, 1, 2, 0, 1};
 * int[] yPred = {0, 1, 1, 0, 2};
 * Classification classification = new Classification(yTrue, yPred, 3);
 * double accuracy = classification.accuracy();
 * double f1Macro = classification.getMetric("f1", "macro");
 * String summary = classification.summary("macro");
 * </pre>
 *
 * <h2>Constructor Summary</h2>
 * <ul>
 *     <li>{@link Classification#Classification(int[], int[], int)} - Constructs a Classification object using true and predicted labels, and the number of classes.</li>
 *     <li>{@link Classification#Classification(int[], int[])} - Constructs a Classification object and automatically detects the number of classes based on the true labels.</li>
 * </ul>
 * 
 * <h2>Getter Summary</h2>
 * <ul>
 *     <li>{@link Classification#getMetric(String, String)} - Returns the requested metric based on the desired averaging method.</li>
 *     <li>{@link Classification#summary(String)} - Returns a string summarizing the key metrics (Accuracy, Precision, Recall, F1 score, MCC, Confusion Matrix).</li>
 * </ul>
 *
 * <h2>Average Metric Types</h2>
 * <h3>Macro Average</h3>
 * <p>
 * Macro averaging calculates the metric for each class individually and then takes the average. It treats all classes equally regardless of their support (the number of samples in each class). 
 * It is useful when you care equally about the performance of each class.
 * </p>
 * <p><strong>Pros:</strong></p>
 * <ul>
 *     <li>Good for balanced datasets where all classes are equally important.</li>
 *     <li>Highlights poor performance on minority classes.</li>
 * </ul>
 * <p><strong>Cons:</strong></p>
 * <ul>
 *     <li>May not be suitable for imbalanced datasets where the class distribution is skewed.</li>
 *     <li>Can lead to misleading results if a small class performs poorly.</li>
 * </ul>
 *
 * <h3>Micro Average</h3>
 * <p>
 * Micro averaging aggregates the true positives, false positives, true negatives, and false negatives across all classes, then calculates the metric. 
 * This method gives equal weight to all samples, regardless of class. 
 * It is useful when you care more about the overall performance of the model than individual class performance.
 * </p>
 * <p><strong>Pros:</strong></p>
 * <ul>
 *     <li>Appropriate for imbalanced datasets where you want to focus on overall model performance.</li>
 *     <li>Less sensitive to imbalanced class distributions.</li>
 * </ul>
 * <p><strong>Cons:</strong></p>
 * <ul>
 *     <li>May mask poor performance on minority classes, as it weights all samples equally.</li>
 * </ul>
 *
 * <h3>Weighted Average</h3>
 * <p>
 * Weighted averaging computes the metric for each class and multiplies it by the class's weight, which is typically the proportion of that class in the dataset. 
 * This method is useful when you want to account for class imbalances by giving more importance to the performance on more frequent classes.
 * </p>
 * <p><strong>Pros:</strong></p>
 * <ul>
 *     <li>Good for imbalanced datasets where you want to prioritize classes with more samples.</li>
 *     <li>Provides a balanced evaluation by taking into account the class distribution.</li>
 * </ul>
 * <p><strong>Cons:</strong></p>
 * <ul>
 *     <li>May underemphasize the performance on minority classes if their weight is too low.</li>
 * </ul>
 */
public class Classification {

    private final int[] tp; // True Positives for each class
    private final int[] tn; // True Negatives for each class
    private final int[] fp; // False Positives for each class
    private final int[] fn; // False Negatives for each class
    private final int numClasses;
    private int _trueLabels = 0; // Total samples in each class
    private int _lengthTrueLables;
    private int[] _classesArr;

    /**
     * Constructor to compute the confusion matrix components from true and predicted labels.
     * 
     * @param yTrue   True labels (ground truth).
     * @param yPred   Predicted labels.
     * @param numClasses  Number of classes in the classification problem.
     */
    // ----------------------------------------------------------------
     // second version for tp,tn,fp,fn
     //----------------------------------------------------------------
    // public Classification(int[] yTrue, int[] yPred, int numClasses) {
    //     if (yTrue.length != yPred.length) {
    //         throw new IllegalArgumentException("The true and predicted label arrays must have the same length.");
    //     }
    //     this.lengthTrueLables = yTrue.length;
    //     this.numClasses = numClasses;
    //     this.classesArr = new int[numClasses];
    //     for (int i = 0; i < numClasses; i++) {
    //         classesArr[i] = i;  // Fill the array with class labels 0 to numClasses - 1
    //     }
    
    //     tp = new int[numClasses];
    //     tn = new int[numClasses];
    //     fp = new int[numClasses];
    //     fn = new int[numClasses];
    
    //     for (int i = 0; i < yTrue.length; i++) {
    //         int trueLabel = yTrue[i];
    //         int predLabel = yPred[i];
    //         if (trueLabel == predLabel) {
    //             this.trueLabels++;
    //             tp[trueLabel]++;
    //         } 
    //         else {
    //             fp[predLabel]++;
    //             fn[trueLabel]++;
    //         }
    //     }
    
    //     // Calculate true negatives
    //     for (int i = 0; i < numClasses; i++) {
    //         int total = lengthTrueLables;
    //         tn[i] = total - tp[i] - fp[i] - fn[i];
    //     }
    // }
     // ----------------------------------------------------------------
     // first version for tp,tn,fp,fn
     //----------------------------------------------------------------
    public Classification(int[] yTrue, int[] yPred, int numClasses) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("The true and predicted label arrays must have the same length.");
        }
        this._lengthTrueLables = yTrue.length;
        this.numClasses = numClasses;
        this._classesArr = new int[numClasses];
        for (int i = 0; i < numClasses; i++) {
            _classesArr[i] = i;  
        }


        tp = new int[numClasses];
        tn = new int[numClasses];
        fp = new int[numClasses];
        fn = new int[numClasses];
    
        for (int i = 0; i < yTrue.length; i++) {
            int trueLabel = yTrue[i];
            int predLabel = yPred[i];
            if (trueLabel == predLabel) {
                this._trueLabels++;
                tp[trueLabel]++;
                // Update true negatives for all classes except the true label
                for (int j = 0; j < numClasses; j++) {
                    if (j != trueLabel) {
                        tn[j]++;
                    }
                }
            } 
            else {
                fp[predLabel]++;
                fn[trueLabel]++;
            }
        }
    }



    public Classification(int[] yTrue, int[] yPred){
        this( yTrue, yPred, (Arrays.stream(yTrue).max().orElse(0) + 1));//auto find teh number of classes in the data provided
    }

    /**
     * Accuracy: Measures the overall correctness of the model.
     * (the proportion of correct predictions (both TP and TN) over all predictions).
     * <p>
     * (Sum of diagonal elements) / (Total number of samples).
     * </p>
     * - Pros: Simple, intuitive, good for balanced datasets.
     * - Cons: Misleading for imbalanced datasets.
     */
    public double accuracy() {
        return (double) _trueLabels/ _lengthTrueLables;
    }

    /**
     * Precision: Measures the proportion of true positives out of all predicted positives.
     * <p>
     * Precision: TP / (TP + FP).
     * </p>
     * - Pros: Useful when false positives are costly (e.g., spam detection).
     * - Cons: May be misleading when there are few true positives.
     */
    public double precision(int classIndex) {
        return tp[classIndex] + fp[classIndex] == 0 ? 0 : (double) tp[classIndex] / (tp[classIndex] + fp[classIndex]);
    }

    /**
     * Recall (Sensitivity): Measures the proportion of true positives out of all actual positives.
     * Useful when we want to minimize false negatives (e.g., in medical diagnoses).
     * <p>
     * Recall (Sensitivity): TP / (TP + FN).
     * </p>
     * - Pros: Useful when false negatives are costly (e.g., disease diagnosis).
     * - Cons: May be misleading when there are few true positives.
     */
    public double recall(int classIndex) {
        return tp[classIndex] + fn[classIndex] == 0 ? 0 : (double) tp[classIndex] / (tp[classIndex] + fn[classIndex]);
    }

    /**
     * Specificity: Measures the proportion of true negatives out of all actual negatives.
     *<p>
     *Specificity: TN / (TN + FP)
     </p>
     */
    public double specificity(int classIndex) {
        return tn[classIndex] + fp[classIndex] == 0 ? 0 : (double) tn[classIndex] / (tn[classIndex] + fp[classIndex]);
    }

    /**
     * F1 Score: Harmonic mean of Precision and Recall. Balances both metrics and is good for imbalanced datasets.
     * <p>
     * F1 Score: 2 * (Precision * Recall) / (Precision + Recall)
     * </p>
     */
    public double f1Score(int classIndex) {
        double precision = precision(classIndex);
        double recall = recall(classIndex);
        return precision + recall == 0 ? 0 : 2 * (precision * recall) / (precision + recall);
    }

    /**
     * Matthews Correlation Coefficient (MCC): A balanced metric for binary classification problems.
     * Ranges from -1 to 1, where 1 is perfect prediction, -1 is total disagreement, and 0 is random prediction.
     * <p>
     *  Matthews Correlation Coefficient (MCC):
     * (TP * TN - FP * FN) / sqrt((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN))
     * </p>
     */
    public double mcc(int classIndex) {
        double numerator = (double) (tp[classIndex] * tn[classIndex] - fp[classIndex] * fn[classIndex]);
        double denominator = Math.sqrt((tp[classIndex] + fp[classIndex]) * (tp[classIndex] + fn[classIndex]) *
                                        (tn[classIndex] + fp[classIndex]) * (tn[classIndex] + fn[classIndex]));
        return denominator == 0 ? 0 : numerator / denominator;
    }

    /**
     * Confusion Matrix: Returns a 2D array [classIndex][TP, TN, FP, FN] for each class.
     */
    public int[][] confusionMatrix() {
        int[][] matrix = new int[numClasses][4];
        for (int i = 0; i < numClasses; i++) {
            matrix[i][0] = tp[i];
            matrix[i][1] = tn[i];
            matrix[i][2] = fp[i];
            matrix[i][3] = fn[i];
        }
        return matrix;
    }

    /**
     * Compute the average of a metric across all classes.
     * Supports 'macro', 'micro', and 'weighted' averages.
     */
    private double averageMetric(String metric, String averageType) {
        switch (averageType.toLowerCase()) {
            case "macro":
                return Arrays.stream(_classesArr).mapToDouble(i -> getMetricForClass(i, metric)).average().orElse(0);
            case "micro":
                return getMicroMetric(metric);
            case "weighted":
                return getWeightedMetric(metric);
            default:
                throw new IllegalArgumentException("Unknown average type: " + averageType);
        }
    }

    private double getMetricForClass(int classIndex, String metric) {
        switch (metric) {
            case "precision":
                return precision(classIndex);
            case "recall":
                return recall(classIndex);
            case "f1":
                return f1Score(classIndex);
            default:
                throw new IllegalArgumentException("Unknown metric: " + metric);
        }
    }

    private double getMicroMetric(String metric) {
        int totalTP = Arrays.stream(tp).sum();
        int totalFP = Arrays.stream(fp).sum();
        int totalFN = Arrays.stream(fn).sum();
            double result = 0.0;

    switch (metric) {
        case "precision":
            result = (totalTP + totalFP == 0) ? 0 : (double) totalTP / (totalTP + totalFP);
            break;
        case "recall":
            result = (totalTP + totalFN == 0) ? 0 : (double) totalTP / (totalTP + totalFN);
            break;
        case "f1":
            double precision = getMicroMetric("precision");
            double recall = getMicroMetric("recall");
            result = (precision + recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);
            break;
        default:
            throw new IllegalArgumentException("Unknown metric: " + metric);
    }

    return result;
}

    private double getWeightedMetric(String metric) {
        double weightedSum = 0;
        for (int i = 0; i < numClasses; i++) {
            double weight = (tp[i] + fn[i]) / (double) _lengthTrueLables;
            weightedSum += weight * getMetricForClass(i, metric);
        }
        return weightedSum;
    }

    /**
     * Returns the requested metric based on the desired average type.
     */
    public double getMetric(String metric, String averageType) {
        if (metric.equalsIgnoreCase("accuracy")) {
            System.out.println("Warning: The 'averageType' is ignored for accuracy metric.");
            return accuracy(); 
        }
        return averageMetric(metric, averageType); // For precision, recall ...
    }

    /**
     * Outputs all metrics as a formatted string.
     */
    public String summary(String averageType) {
        return String.format(
            "Accuracy: %.4f%n" +
            "Precision: %.4f%n" +
            "Recall: %.4f%n" +
            "F1 Score: %.4f%n" +
            "Confusion Matrix: %s%n",
            accuracy(), getMetric("precision", averageType), getMetric("recall", averageType),
            getMetric("f1", averageType), Arrays.deepToString(confusionMatrix())
        );
    }
}
