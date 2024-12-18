package net.acimon.jmlearn.models.neighbors;
import net.acimon.jmlearn.models.Model;

import net.acimon.jmlearn.utils.EuclideanDistance;
import net.acimon.jmlearn.utils.ManhattanDistance;
import net.acimon.jmlearn.metrics.Accuracy;


import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * The K-Nearest Neighbors (KNN) algorithm for classification tasks.
 * <p>
 * This class provides a simple implementation of the KNN algorithm, including methods for training and predicting class labels.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Train the model with labeled data using the {@code fit} method.</li>
 *     <li>Make predictions on new data using the {@code predict} method.</li>
 *     <li>Calculate classification accuracy using the static {@code accuracy} method that call {@code Accuracy.calculate}.</li>
 *     <li>Custom exceptions for invalid k values and empty datasets.</li>
 * </ul>
 * 
 *
 * @see EuclideanDistance for euclidean distance calculation.
 * @see ManhattanDistance for manhattan distance calculation (L1).
 * @see Accuracy for accuracy calculation.
 *
 * Constructors Summary:
 * <ul>
 *     <li>{@link KNN#KNN(int, String)} - Constructs a KNN object with a specified number of neighbors (k) and a distance metric ("euclidean" or "manhattan").</li>
 *     <li>{@link KNN#KNN()} - Constructs a KNN object with default settings (k = 3 and Euclidean distance).</li>
 * </ul>
 */
public class KNN implements Model{
    private int _k; // The number of neighbors to consider
    private double[][] _X_train; // Training data 
    private int[] _Y_train; // Training labels
    private String _distanceMetric; // Distance metric (Euclidean, Manhattan)
    private static final int DEFAULT_K = 3;
    private static final String[] VALID_DISTANCE_METRICS = {"euclidean", "manhattan"};

    /**
     * Constructor for KNN classifier.
     * 
     * @param k The number of nearest neighbors to consider for classification.
     * @param distanceMetric The distance metric to use: "euclidean", "manhattan".
     * @throws IllegalArgumentException if k is less than 1 or the distance metric is invalid.
     */
    public KNN(int k, String distanceMetric) {
        setK(k);
        setDistanceMetric(distanceMetric);
    }

    /**
     * Default constructor for KNN classifier with a default value of k and Euclidean distance.
     * <p>
     * This constructor sets the value of k to the default value, which is 3, and uses Euclidean distance.
     * </p>
     */
    public KNN() {
        this(DEFAULT_K,VALID_DISTANCE_METRICS[0]);
    }

    public KNN(KNN other) {
        this._k = other._k; 
        this._distanceMetric = other._distanceMetric; 

        if (other._X_train != null){
            this._X_train = new double[other._X_train.length][];
            for (int i = 0; i < other._X_train.length; i++) {
                this._X_train[i] = other._X_train[i].clone(); // Deep copy of each row in the 2D array
            }

            this._Y_train = other._Y_train.clone(); // Deep copy of the 1D array
        }
    }
    public Model clone(){
        return (new KNN(this));
    }
    /**
     * Sets the number of neighbors to consider for classification.
     * 
     * @param k The number of nearest neighbors to consider.
     * @throws IllegalArgumentException if k is less than 1.
     */
    public void setK(int k) {
        if (k < 1) {
            throw new IllegalArgumentException("The value of k must be greater than zero.");
        }
        this._k = k;
    }
    public int getK(){
        return (this._k);

    }
    public String getDistanceMetric(){
        return (this._distanceMetric);
    }

    /**
     * Sets the distance metric to use: "euclidean", "manhattan".
     * 
     * @param distanceMetric The distance metric to use.
     * @throws IllegalArgumentException if the distance metric is invalid.
     */
    public void setDistanceMetric(String distanceMetric) {
        if (!Arrays.asList(VALID_DISTANCE_METRICS).contains(distanceMetric)){
            throw new IllegalArgumentException("Invalid distance metric. Must be one of: " + String.join(", ", VALID_DISTANCE_METRICS) + ".");
        }
        this._distanceMetric = distanceMetric;
    }

    /**
     * Fits the KNN model to the training data.
     * 
     * @param X The training data features.
     * @param Y The training data labels.
     * @throws IllegalArgumentException if the training data arrays are empty or feature dimensions don't match.
     */
    public void fit(double[][] X, int[] Y) {
        if (X.length == 0 || Y.length == 0) {
            throw new IllegalArgumentException("Training data cannot be empty.");
        }
        if (X.length != Y.length) {
            throw new IllegalArgumentException("The number of samples in X and Y must match.");
        }
        int numFeatures = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != numFeatures) {
                throw new IllegalArgumentException("All feature vectors must have the same number of dimensions.");
            }
        }
        this._X_train = X;
        this._Y_train = Y;
    }
    @Override
    public void fit(double[][] dataPoints) {
        // Not applicable for unsupervised models
        throw new UnsupportedOperationException("fit method without labels not supported for supervised model");
    }

    /**
     * Makes predictions based on the test data using the trained KNN model.
     * 
     * @param X The test data features.
     * @return An array of predicted class labels.
     * @throws IllegalArgumentException if the test data array is empty or feature dimensions don't match.
     */
    @Override
    public int[] predict(double[][] X) {
        if (X.length == 0) {
            throw new IllegalArgumentException("Test data cannot be empty.");
        }

        int numFeatures = _X_train[0].length;
        for (int i = 0; i < X.length; i++) {
            if (X[i].length != numFeatures) {
                throw new IllegalArgumentException("The number of features in the test data must match the training data.");
            }
        }

        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = _predict(X[i]);
        }
        return predictions;
    }
  
    /**
     * Calculates the classification accuracy by comparing the true labels to the predicted labels.
     * 
     * @param y_true The true class labels.
     * @param y_pred The predicted class labels.
     * @return The classification accuracy.
     */
    public static double accuracy(int[] y_true, int[] y_pred) {
        return Accuracy.calculate(y_true, y_pred);
    }
    

    /**
     * Helper method for predicting the class of a single test point.
     * 
     * @param x The test point to classify.
     * @return The predicted class label.
     */
    private int _predict(double[] x) {
        // Compute distances from x to each training example
        double[] distances = new double[_X_train.length];
        for (int i = 0; i < _X_train.length; i++) {
            switch (_distanceMetric) {
                case "manhattan":
                    distances[i] = ManhattanDistance.calculate(x, _X_train[i]);
                    break;
                case "euclidean":
                default:
                    distances[i] = EuclideanDistance.calculate(x, _X_train[i]);
                    break;
            }
        }

        // initialize an array with the indices of _X_train as values.
        Integer[] kIndexes = new Integer[_X_train.length];
        for (int i = 0; i < _X_train.length; i++) {
            kIndexes[i] = i;
        }

        // sort the indices array in ascending order based on the distances stored in the distances array.
        // meaning that kIndexes[0] will hold the index of the _X_train sample with the shortest distance.
        Arrays.sort(kIndexes, (i, j) -> Double.compare(distances[i], distances[j]));

  

        // Count the occurrences of each label among the nearest k neighbors 
        Map<Integer, Integer> labelCounts = new HashMap<>();
        for (int i = 0; i < _k; i++) {
            int label = _Y_train[kIndexes[i]];
            // put the label in HashMap where the label is key and value is num of occurrences.
            // getOrDefault(key, defaultValue) returns the value for the given key if it exists, otherwise returns defaultValue (set to 0).
            // than we add 1 to increment the occurrences if exists -> value+1 if not -> 0+1
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
        }

        // Return the most common label (the key(lable) of the highest value of in the Map meaning most occurrences).
        return labelCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}
