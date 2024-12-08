package net.acimon.jmlearn.models.cluster;


import net.acimon.jmlearn.utils.EuclideanDistance;
import net.acimon.jmlearn.utils.PlotData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// visualizetion 
import javax.swing.JFrame;
import java.awt.Dimension;
import org.jfree.chart.ChartPanel;

/**
 * The KMeans class implements the KMeans clustering algorithm for unsupervised learning tasks.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Performs clustering by partitioning data into K clusters.</li>
 *     <li>Supports visualization of clustering steps.</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * double[][] data = {{1.0, 2.0}, {1.5, 1.8}, {5.0, 8.0}, {8.0, 8.0}, {1.0, 0.6}, {9.0, 11.0}};
 * KMeans kmeans = new KMeans(3, 100, 42L, true);
 * kmeans.fit(data);
 * </pre>
 *
 * <h2>Constructor Summary</h2>
 * <ul>
 *     <li>{@link KMeans#KMeans(int, int, Long, boolean)} - Constructs a KMeans object with specified number of clusters, maximum iterations, seed, and plotting option.</li>
 *     <li>{@link KMeans#KMeans()} - Constructs a KMeans object with default settings (K=5, max iterations=100, no visualization).</li>
 *     <li>{@link KMeans#KMeans(int, int, boolean)} - Constructs a KMeans object with a specified number of clusters, maximum iterations, and a plotting option.</li>
 *     <li>{@link KMeans#KMeans(int, int, Long)} - Constructs a KMeans object with a specified number of clusters, maximum iterations, and random seed.</li>
 * </ul>
 *
 * <h2>Visualization</h2>
 * <p>
 * The class optionally provides a visualization of the clustering steps using JFreeChart.
 * The `plotSteps` flag in the constructor enables or disables the visualization.
 * </p>
 *
*/
public class KMeans {
    private int _k; // Number of clusters
    private int _maxIter; // Maximum number of iterations
    private double[][] _X; // Input data
    private double[][] _centroids; // Cluster centroids
    private List<List<Integer>> _clusters; // Cluster assignments
    private boolean _plotSteps; 
    private Random _random;
    private Long _seed;
    private static final int DEFAULT_K = 5;
    private static final int DEFAULT_MAX_ITER = 100;
    private static final double CONVERGED_FACTOR = 1e-4;

    // For visualization
    private JFrame _frame = new JFrame("KMeans Clustering");
    private ChartPanel _chartPanel = new ChartPanel(null);  // Start with no chart


    /**
     * Constructor for KMeans clustering with user-specified number of clusters, maximum iterations, seed, and visualization option.
     * 
     * @param k The number of clusters to create.
     * @param maxIter Maximum number of iterations.
     * @param seed The random seed for initializing centroids.
     * @param plotSteps Whether to plot the clustering steps.
     */
    public KMeans(int k, int maxIter, Long seed, boolean plotSteps) {
        this._k = k;
        this._maxIter = maxIter;
        this._clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            _clusters.add(new ArrayList<>());
        }

        this._plotSteps = plotSteps;
        this._seed = seed;
        this._random = (seed != null) ? new Random(seed):new Random();

        if (plotSteps){
            _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            _chartPanel.setPreferredSize(new Dimension(800, 600));
            _frame.getContentPane().add(_chartPanel);
            _frame.pack();
            _frame.setVisible(true);
        }
    }

    /**
     * Default constructor for KMeans with default k and max iterations.
     */
    public KMeans() {
        this(DEFAULT_K, DEFAULT_MAX_ITER,null, false);
    }
    public KMeans(int k, int maxIter, boolean plotSteps) {
        this(k, maxIter,null, plotSteps);
    }
    public KMeans(int k, int maxIter, Long seed) {
        this(k, maxIter,seed,false );
    }
    public KMeans(int k, int maxIter) {
        this(k, maxIter,null,false );
    }

    /**
     * Fits the KMeans model to the input data.
     * 
     * @param X Input data, where each row is a sample and each column is a feature.
     */
    public void fit(double[][] X) {
        this._X = X;
        int nSamples = X.length;
        int nFeatures = X[0].length;

        this._centroids = new double[_k][nFeatures];
        
        // Initialize centroids by choosing random points from the data
        for (int i = 0; i < _k; i++) {
            int randomIndex = this._random.nextInt(nSamples);
            this._centroids[i] = Arrays.copyOf(X[randomIndex], nFeatures);
        }

        for (int iter = 0; iter < _maxIter; iter++) {
            // Step 1: Assign samples to nearest centroids
            _clusters = _createClusters();

            // Plot centroid updates live
            if (_plotSteps) {
                plotLiveClusters(iter, _X, _centroids, _clusters);
            }

            // Step 2: Recompute centroids
            double[][] oldCentroids = _centroids;
            _centroids = _getCentroids();
            // Plot cluster assignments after centroids update
            if (_plotSteps) {
                plotLiveClusters(iter, _X, _centroids, _clusters);
            }

            // Step 3: Check for convergence
            if (_isConverged(oldCentroids, _centroids)) {
                break;
            }


        }
    }

    /**
     * Assigns each sample to the closest centroid.
     * 
     * @return List of clusters, each containing the indices of samples in that cluster.
     */
    private List<List<Integer>> _createClusters() {
        List<List<Integer>> clusters = new ArrayList<>(_k);
        for (int i = 0; i < _k; i++) {
            clusters.add(new ArrayList<>());
        }

        for (int i = 0; i < _X.length; i++) {
            double[] sample = _X[i];
            int closestCentroidIndex = _closestCentroid(sample);
            clusters.get(closestCentroidIndex).add(i);
        }

        return clusters;
    }

    /**
     * Finds the index of the closest centroid for a given sample.
     * 
     * @param sample The data point.
     * @return Index of the closest centroid.
     */
    private int _closestCentroid(double[] sample) {
        double minDistance = Double.MAX_VALUE;
        int closestCentroidIndex = -1;
        for (int i = 0; i < _centroids.length; i++) {
            double distance = EuclideanDistance.calculate(sample, _centroids[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestCentroidIndex = i;
            }
        }
        return closestCentroidIndex;
    }

    /**
     * Recomputes the centroids by averaging the samples in each cluster.
     * 
     * @return The new centroids.
     */
    private double[][] _getCentroids() {
        double[][] centroids = new double[_k][_X[0].length];
        for (int i = 0; i < _k; i++) {
            List<Integer> cluster = _clusters.get(i);
            double[] sum = new double[_X[0].length];
            for (int index : cluster) {
                for (int j = 0; j < _X[index].length; j++) {
                    sum[j] += _X[index][j];
                }
            }
            for (int j = 0; j < sum.length; j++) {
                centroids[i][j] = sum[j] / cluster.size();
            }
        }
        return centroids;
    }

    /**
     * Checks if the centroids have converged (i.e., no change between iterations).
     * 
     * @param oldCentroids The centroids from the previous iteration.
     * @param centroids The current centroids.
     * @return True if the centroids have converged, false otherwise.
     */
    private boolean _isConverged(double[][] oldCentroids, double[][] centroids) {
        for (int i = 0; i < _k; i++) {
            if (EuclideanDistance.calculate(oldCentroids[i], centroids[i]) > CONVERGED_FACTOR) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes predictions by assigning each sample to the nearest centroid.
     * 
     * @param X The input data.
     * @return An array of predicted cluster labels.
     */
    public int[] predict(double[][] X) {
        int[] labels = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            labels[i] = _closestCentroid(X[i]);
        }
        return labels;
    }
        /**
     * Transforms the input data X into a cluster-distance space.
     * Each dimension is the distance to the cluster centers.
     * 
     * @param X The input data.
     * @return A 2D array where each row is the distance of a sample to each cluster centroid.
     */
    public double[][] transform(double[][] X) {
        double[][] distances = new double[X.length][_k];
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < _k; j++) {
                distances[i][j] = EuclideanDistance.calculate(X[i], _centroids[j]);
            }
        }
        return distances;
    }

    /**
     * Combines the fit and transform methods—fits the model and then transforms X into cluster-distance space.
     * 
     * @param X The input data.
     * @return A 2D array where each row is the distance of a sample to each cluster centroid.
     */
    public double[][] fitTransform(double[][] X) {
        fit(X); // Fit the model first
        return transform(X); // Then transform to cluster-distance space
    }

    /**
     * Combines the fit and predict methods—first fits the model and then predicts the cluster labels.
     * 
     * @param X The input data.
     * @return An array of predicted cluster labels.
     */
    public int[] fitPredict(double[][] X) {
        fit(X); // Fit the model first
        return predict(X); // Then predict the labels
    }

    /**
     * Calculates "Inertia" score based on the sum of squared distances between samples and their assigned centroids.
     * 
     * @return The inertia score (sum of squared distances).
     */
    public double score() {
        double score = 0.0;
        for (int i = 0; i < _X.length; i++) {
            int closestCentroidIndex = _closestCentroid(_X[i]);
            score += EuclideanDistance.calculate(_X[i], _centroids[closestCentroidIndex], true);
        }
        return score;
    }


    /**
     * Plot function to show clusters and centroids live.
     *
     * @param iter The current iteration.
     * @param data The data points.
     * @param centroids The centroids.
     * @param clusters The cluster assignments.
     */
    private void plotLiveClusters(int iter, double[][] data, double[][] centroids, List<List<Integer>> clusters) {
        List<double[]> points = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
    
        // Prepare points and labels for PlotData.plotData
        for (int i = 0; i < data.length; i++) {
            points.add(new double[]{data[i][0], data[i][1]});
            int clusterIndex = _closestCentroid(data[i]);
            labels.add(clusterIndex);
        }
        try {
            // Slow down the loop to visualize each step (500ms delay)
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        // Plot the clusters with centroids
        // Now, we plot the centroids as well
        PlotData.plotDataWithCentroids(points, labels, centroids, "KMeans Iteration " + iter, _frame, _chartPanel);
    }
    
    // Getters for K, max iterations, etc.
    public int getK() {
        return _k;
    }

    public int getMaxIter() {
        return _maxIter;
    }

    public double[][] getCentroids() {
        return _centroids;
    }

    public List<List<Integer>> getClusters() {
        return _clusters;
    }
    public Long getSeed(){
        return this._seed;
    }
}
