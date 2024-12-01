package net.acimon.jmlearn.utils;

import java.util.*;

public class DatasetSplitter {

    private final double validationRatio;  // Ratio of validation set (0.0 to 1.0)
    private final long seed;               // Seed for reproducibility
    private final Random random;           // Random object for reproducibility

    // Constructor with validation ratio and seed for reproducibility
    public DatasetSplitter(double validationRatio, long seed) {
        if (validationRatio < 0.0 || validationRatio > 1.0) {
            throw new IllegalArgumentException("Validation ratio must be between 0 and 1");
        }
        this.validationRatio = validationRatio;
        this.seed = seed;
        this.random = new Random(seed);
    }

    public long getSeed(){
        return this.seed;
    }

    // Method to split the features and labels into training and validation sets
    public SplitResult split(double[][] features, int[] labels) {
        // Check if the feature and label arrays have the same length
        if (features.length != labels.length) {
            throw new IllegalArgumentException("Features and labels must have the same number of rows");
        }

        // Number of samples
        int totalSamples = features.length;

        // Create an index array to shuffle
        List<Integer> indices = new ArrayList<>(totalSamples);
        for (int i = 0; i < totalSamples; i++) {
            indices.add(i);
        }

        // Shuffle the indices
        Collections.shuffle(indices, random);

        // Calculate the split index
        int splitIndex = (int) (totalSamples * (1 - validationRatio));

        // Split the indices for training and validation
        List<Integer> trainIndices = indices.subList(0, splitIndex);
        List<Integer> valIndices = indices.subList(splitIndex, totalSamples);

        // Create the training and validation data
        double[][] trainFeatures = new double[trainIndices.size()][];
        int[] trainLabels = new int[trainIndices.size()];  // Corrected to int[]
        double[][] valFeatures = new double[valIndices.size()][];
        int[] valLabels = new int[valIndices.size()];  // Corrected to int[]

        // Populate the training and validation data
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

        // Return the result
        return new SplitResult(trainFeatures, trainLabels, valFeatures, valLabels);
    }

    // Result class to hold the split data
    public static class SplitResult {
        private final double[][] trainFeatures;
        private final int[] trainLabels;   // Corrected to int[]
        private final double[][] valFeatures;
        private final int[] valLabels;     // Corrected to int[]

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
