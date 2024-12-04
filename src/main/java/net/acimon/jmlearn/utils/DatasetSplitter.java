package net.acimon.jmlearn.utils;

import java.util.*;

/**
 * The DatasetSplitter class provides methods to split a dataset into training and validation sets.
 * <p>
 * This class supports splitting features and labels based on a given validation ratio. It performs
 * a random shuffle of the dataset to ensure a randomized split. The class also supports reproducibility
 * by using a seed value for the random number generator.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * DatasetSplitter splitter = new DatasetSplitter(0.2, 42L); // 20% validation set and a specific seed for reproducibility
 * DatasetSplitter.SplitResult result = splitter.split(features, labels);
 * double[][] trainFeatures = result.getTrainFeatures();
 * int[] trainLabels = result.getTrainLabels();
 * double[][] valFeatures = result.getValFeatures();
 * int[] valLabels = result.getValLabels();
 * </pre>
 *
 * <h2>Constructor Summary</h2>
 * <ul>
 *     <li>{@link DatasetSplitter#DatasetSplitter(double, Long)} - Constructs a DatasetSplitter with the validation ratio and an optional seed for randomization.</li>
 *     <li>{@link DatasetSplitter#DatasetSplitter(double)} - Constructs a DatasetSplitter with the validation ratio and uses a random seed.</li>
 * </ul>
 *
 * <h2>Getter Methods</h2>
 * <ul>
 *     <li>{@link DatasetSplitter#getSeed()} - Retrieves the seed value used for randomization.</li>
 * </ul>
 *
 * <h3>Nested Class</h3>
 * <ul>
 *     <li>{@link DatasetSplitter.SplitResult} - Holds the results of the split, containing training and validation features and labels.</li>
 * </ul>
 */

public class DatasetSplitter {

    private final double validationRatio;  //(0.0 to 1.0)
    private final Random random;
    private long _seed;           

    
    public DatasetSplitter(double validationRatio, Long seed) {
        if (validationRatio < 0.0 || validationRatio > 1.0) {
            throw new IllegalArgumentException("Validation ratio must be between 0 and 1");
        }
        this.validationRatio = validationRatio;

        // Use seed if available, otherwise initialize with a random seed
        if (seed != null) {
            this._seed = seed;
            this.random = new Random(seed);
        } else {
            this.random = new Random();  
        }
    }
    public DatasetSplitter(double validationRatio){
        this(validationRatio, null);
    }

    public long getSeed(){
        return this._seed;
    }

    /**
     * Splits the dataset into training and validation sets.
     * <p>
     * This method accepts the features and labels as input arrays, shuffles them randomly, and splits them 
     * into training and validation sets based on the specified validation ratio.
     * </p>
     *
     * @param features The feature data, where each row represents a sample.
     * @param labels The corresponding label data.
     * @return A {@link DatasetSplitter.SplitResult} object containing the training and validation features and labels.
     * @throws IllegalArgumentException If the features and labels arrays do not have the same number of rows.
     */
    public SplitResult split(double[][] features, int[] labels) {
        
        if (features.length != labels.length) {
            throw new IllegalArgumentException("Features and labels must have the same number of rows");
        }

        
        int totalSamples = features.length;

        // Create an index array to shuffle
        List<Integer> indices = new ArrayList<>(totalSamples);
        for (int i = 0; i < totalSamples; i++) {
            indices.add(i);
        }

        
        Collections.shuffle(indices, random);

        // Calculate the split index
        int splitIndex = (int) (totalSamples * (1 - validationRatio));

        
        List<Integer> trainIndices = indices.subList(0, splitIndex);
        List<Integer> valIndices = indices.subList(splitIndex, totalSamples);

        // Create the training and validation sets
        double[][] trainFeatures = new double[trainIndices.size()][];
        int[] trainLabels = new int[trainIndices.size()];  
        double[][] valFeatures = new double[valIndices.size()][];
        int[] valLabels = new int[valIndices.size()]; 

        // Populate training and validation data
        for (int i = 0; i < trainIndices.size(); i++) {
            int index = trainIndices.get(i);
            trainFeatures[i] = features[index];
            trainLabels[i] = (int) labels[index];  // Cast to int if labels are doubles
        }

        for (int i = 0; i < valIndices.size(); i++) {
            int index = valIndices.get(i);
            valFeatures[i] = features[index];
            valLabels[i] = (int) labels[index];  // Cast to int if labels are doubles
        }

        return new SplitResult(trainFeatures, trainLabels, valFeatures, valLabels);
    }

    /**
     * Class to hold the results of the dataset split, containing both the training and validation features and labels.
     */
    public static class SplitResult {
        private final double[][] trainFeatures;
        private final int[] trainLabels;   
        private final double[][] valFeatures;
        private final int[] valLabels;    

        public SplitResult(double[][] trainFeatures, int[] trainLabels, double[][] valFeatures, int[] valLabels) {
            this.trainFeatures = trainFeatures;
            this.trainLabels = trainLabels;
            this.valFeatures = valFeatures;
            this.valLabels = valLabels;
        }

        public double[][] getTrainFeatures() {
            return trainFeatures;
        }

        public int[] getTrainLabels() {  // Corrected to return int[]
            return trainLabels;
        }

        public double[][] getValFeatures() {
            return valFeatures;
        }

        public int[] getValLabels() {  // Corrected to return int[]
            return valLabels;
        }
    }
}
