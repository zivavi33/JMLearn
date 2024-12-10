package net.acimon.jmlearn.models.tree;

import net.acimon.jmlearn.metrics.Accuracy;
import net.acimon.jmlearn.models.Model;
import net.acimon.jmlearn.models.ensemble.Bagging;

import java.util.*;
import java.util.logging.Logger;

/**
 * A Decision Tree classifier for classification tasks.
 */
public class DecisionTree implements Model {

    private int _minSamplesSplit;
    private int _maxDepth;
    private int _nFeatures;
    private Node root;
    private static final Logger logger = Logger.getLogger(Bagging.class.getName());

    // Default values
    private static final int DEFAULT_MIN_SAMPLES_SPLIT = 2;
    private static final int DEFAULT_MAX_DEPTH = 100;
    private static final int DEFAULT_N_FEATURES = -1; // Use -1 to indicate "use all features".

    public DecisionTree(int minSamplesSplit, int maxDepth, int nFeatures) {
        this._minSamplesSplit = minSamplesSplit;
        this._maxDepth = maxDepth;
        this._nFeatures = nFeatures;
    }

    public DecisionTree(int maxDepth) {
        this(DEFAULT_MIN_SAMPLES_SPLIT, maxDepth, DEFAULT_N_FEATURES);
    }

    public DecisionTree() {
        this(DEFAULT_MIN_SAMPLES_SPLIT, DEFAULT_MAX_DEPTH, DEFAULT_N_FEATURES);
    }

    public DecisionTree(DecisionTree other) {
        this._minSamplesSplit = other._minSamplesSplit;
        this._maxDepth = other._maxDepth;
        this._nFeatures = other._nFeatures;
        if (other.root != null) {
            this.root = cloneNode(other.root);
        }
    }
    public int getMinSamplesSplit() {
        return _minSamplesSplit;
    }
    public int getMaxDepth() {
        return _maxDepth;
    }
    public int getNFeatures() {
        return _nFeatures;
    }
    private Node cloneNode(Node original) {
        if (original == null) return null;
        
        Node clone = new Node(original.value);
        clone.feature = original.feature;
        clone.threshold = original.threshold;
        clone.left = cloneNode(original.left);
        clone.right = cloneNode(original.right);
        
        return clone;
    }

    @Override
    public Model clone() {
        return new DecisionTree(this);
    }

    /**
     * Fits the DecisionTree model to the training data.
     *
     * @param X The training data features.
     * @param Y The training data labels.
     */
    public void fit(double[][] X, int[] Y) {
        // Validate input
        if (X == null || Y == null || X.length == 0 || Y.length == 0 || X.length != Y.length) {
            throw new IllegalArgumentException("Invalid input data");
        }
        
        this.root = growTree(X, Y, 0);
    }

    public void fit(double[][] X) {
        logger.severe("fit method without labels not supported for DecisionTree model");
        throw new UnsupportedOperationException("fit method without labels not supported for DecisionTree model");
    }

    /**
     * Recursively grows the tree.
     */
    private Node growTree(double[][] X, int[] Y, int depth) {
        // Determine number of features to use
        int totalFeatures = X[0].length;
        int featuresToConsider = (_nFeatures == -1) ? totalFeatures : Math.min(_nFeatures, totalFeatures);

        int nSamples = X.length;
        int nLabels = (int) Arrays.stream(Y).distinct().count();

        // Stopping criteria
        if (depth >= _maxDepth || nLabels == 1 || nSamples < _minSamplesSplit) {
            return new Node(mostCommonLabel(Y));
        }

        // Select features to consider
        Set<Integer> selectedFeatures = new HashSet<>();
        Random random = new Random();
        while (selectedFeatures.size() < featuresToConsider) {
            selectedFeatures.add(random.nextInt(totalFeatures));
        }

        double bestGain = -1;
        int bestFeature = -1;
        double bestThreshold = -1;
        int[] bestLeftIndices = null;
        int[] bestRightIndices = null;

        // Search for the best split
        for (int featureIndex : selectedFeatures) {
            // Extract the feature column
            double[] featureColumn = new double[X.length];
            for (int i = 0; i < X.length; i++) {
                featureColumn[i] = X[i][featureIndex];
            }

            // Sort the feature column
            double[] sortedFeatureColumn = Arrays.copyOf(featureColumn, featureColumn.length);
            Arrays.sort(sortedFeatureColumn);

            // Try different thresholds
            for (double threshold : sortedFeatureColumn) {
                // Split the data
                List<Integer> leftIndices = new ArrayList<>();
                List<Integer> rightIndices = new ArrayList<>();
                
                for (int i = 0; i < X.length; i++) {
                    if (X[i][featureIndex] <= threshold) {
                        leftIndices.add(i);
                    } else {
                        rightIndices.add(i);
                    }
                }

                // Skip if split is trivial
                if (leftIndices.isEmpty() || rightIndices.isEmpty()) continue;

                
                double gain = informationGain(Y, 
                    leftIndices.stream().mapToInt(Integer::intValue).toArray(), 
                    rightIndices.stream().mapToInt(Integer::intValue).toArray()
                );

                // Update best split if this is better
                if (gain > bestGain) {
                    bestGain = gain;
                    bestFeature = featureIndex;
                    bestThreshold = threshold;
                    bestLeftIndices = leftIndices.stream().mapToInt(Integer::intValue).toArray();
                    bestRightIndices = rightIndices.stream().mapToInt(Integer::intValue).toArray();
                }
            }
        }

        // If no good split found, create a leaf node
        if (bestFeature == -1) {
            return new Node(mostCommonLabel(Y));
        }

        // Recursively build subtrees
        double[][] leftX = new double[bestLeftIndices.length][];
        int[] leftY = new int[bestLeftIndices.length];
        for (int i = 0; i < bestLeftIndices.length; i++) {
            leftX[i] = X[bestLeftIndices[i]];
            leftY[i] = Y[bestLeftIndices[i]];
        }

        double[][] rightX = new double[bestRightIndices.length][];
        int[] rightY = new int[bestRightIndices.length];
        for (int i = 0; i < bestRightIndices.length; i++) {
            rightX[i] = X[bestRightIndices[i]];
            rightY[i] = Y[bestRightIndices[i]];
        }

        Node left = growTree(leftX, leftY, depth + 1);
        Node right = growTree(rightX, rightY, depth + 1);

        return new Node(bestFeature, bestThreshold, left, right);
    }

    /**
     * Calculates the information gain for a given split.
     */
    private double informationGain(int[] Y, int[] leftIdx, int[] rightIdx) {
        double parentEntropy = entropy(Y);
        
        int[] leftY = Arrays.stream(leftIdx).map(i -> Y[i]).toArray();
        int[] rightY = Arrays.stream(rightIdx).map(i -> Y[i]).toArray();
        
        double leftEntropy = entropy(leftY);
        double rightEntropy = entropy(rightY);
        
        int total = Y.length;
        double weightedChildEntropy = 
            (leftIdx.length / (double) total) * leftEntropy +
            (rightIdx.length / (double) total) * rightEntropy;

        return parentEntropy - weightedChildEntropy;
    }

    /**
     * Calculates the entropy for a given set of labels.
     */
    private double entropy(int[] Y) {
        if (Y.length == 0) return 0.0;

        Map<Integer, Integer> counts = new HashMap<>();
        for (int label : Y) {
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }

        double entropy = 0.0;
        for (int count : counts.values()) {
            double prob = (double) count / Y.length;
            entropy -= prob * Math.log(prob) / Math.log(2);
        }
        return entropy;
    }

    /**
     * Finds the most common label in the dataset.
     */
    private int mostCommonLabel(int[] Y) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int label : Y) {
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow(() -> new IllegalStateException("No labels found"));
    }

    /**
     * Makes predictions using the trained decision tree.
     */
    @Override
    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = traverseTree(X[i], root);
        }
        return predictions;
    }

    /**
     * Traverses the decision tree for a given sample.
     */
    private int traverseTree(double[] sample, Node node) {
        // Null check for robustness
        if (node == null) {
            throw new IllegalStateException("Encountered null node during prediction");
        }

        // Leaf node check
        if (node.isLeafNode()) {
            return node.value;
        }

        // Validate feature index
        if (node.feature < 0 || node.feature >= sample.length) {
            throw new IllegalStateException("Invalid feature index: " + node.feature);
        }

        // Traverse left or right based on threshold
        if (sample[node.feature] <= node.threshold) {
            return traverseTree(sample, node.left);
        } else {
            return traverseTree(sample, node.right);
        }
    }

    /**
     * Calculates the accuracy of predictions.
     */
    public static double accuracy(int[] yTrue, int[] yPred) {
        return Accuracy.calculate(yTrue, yPred);
    }

    private static class Node {
        int feature = -1;
        double threshold = Double.NaN;
        Node left;
        Node right;
        int value;

        Node(int value) {
            this.value = value;
        }

        Node(int feature, double threshold, Node left, Node right) {
            this.feature = feature;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
        }

        boolean isLeafNode() {
            return left == null && right == null;
        }
    }
}