package net.acimon.jmlearn.datasets;

import net.acimon.jmlearn.utils.CSVLoader;
import net.acimon.jmlearn.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The SyntheticDataGenerator class provides methods to generate synthetic datasets for machine learning tasks.
 * <p>
 * This class includes methods for generating datasets with different patterns, such as well-separated blobs, 
 * interleaving half circles (moons), and concentric circles, with options to control noise and clustering.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Generate well-separated blobs using {@code makeBlobs}.</li>
 *     <li>Create interleaving half circles (moons) with added noise using {@code makeMoons}.</li>
 *     <li>Generate concentric circles with noise and customizable scaling factor using {@code makeCircles}.</li>
 * </ul>
 *
 * see {@link Pair} for representing a pair of lists (data and labels).
 * 
 * <h2>Constructor Summary</h2>
 * <ul>
 *     <li>{@link SyntheticDataGenerator#SyntheticDataGenerator(Long)} - Constructs a datagenerator with random seed (accepts null).</li>
 *     <li>{@link SyntheticDataGenerator#SyntheticDataGenerator()} - Constructs a datagenerator with random seed.</li>
 * </ul>
 */

public class SyntheticDataGenerator {

    private Random random;



    public SyntheticDataGenerator(Long seed) {
        if (seed != null) {
            this.random = new Random(seed);
        } else {
            this.random = new Random();
        }
    }


    public SyntheticDataGenerator() {
        this(null);
    }

        /**
     * Generates n-dims dataset with well-separated blobs.
     * <p>
     * Each blob is centered at a randomly generated point, and data points are distributed around these centers
     * with a specified standard deviation.
     * </p>
     *
     * @param nSamples   The total number of data points to generate.
     * @param nClusters  The number of clusters (blobs).
     * @param clusterStd The standard deviation of the blobs.
     * @param nDimensions The number of dimensions for each data point.
     * @return A {@link Pair} containing the generated data points and their corresponding labels.
     */
    public Pair<List<double[]>, List<Integer>> makeBlobs(int nSamples, int nClusters, double clusterStd, int nDimensions) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        List<double[]> centroids = new ArrayList<>();
        
        
        for (int i = 0; i < nClusters; i++) {
            double[] centroid = new double[nDimensions];
            for (int j = 0; j < nDimensions; j++) {
                centroid[j] = random.nextDouble() * 10;  // Random centroid position
            }
            centroids.add(centroid);
        }

        // Generate data points around the centroids and assign labels
        for (int i = 0; i < nSamples; i++) {
            int clusterIndex = random.nextInt(nClusters);
            double[] centroid = centroids.get(clusterIndex);
            double[] point = new double[nDimensions];
            
           
            for (int j = 0; j < nDimensions; j++) {
                point[j] = centroid[j] + random.nextGaussian() * clusterStd;
            }
            data.add(point);
            labels.add(clusterIndex);  // Assign label based on the cluster
        }

        return new Pair<>(data, labels);  
    }

    /**
     * Generates a 2D dataset of interleaving half circles (moons) with added noise.
     * <p>
     * The data points are generated in two semi-circular shapes.
     * </p>
     *
     * @param nSamples The total number of data points to generate.
     * @param noise The noise level added to the data points.
     * @return A {@link Pair} containing the generated data points and their corresponding labels.
     */
    public Pair<List<double[]>, List<Integer>> makeMoons(int nSamples, double noise) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
    
        
        double verticalShift = 0.25; 
    
        // points for the first moon (upper half-circle)
        for (int i = 0; i < nSamples / 2; i++) {
            double[] point1 = new double[2];
            double angle = Math.PI * i / (nSamples / 2);  // Angle 
            point1[0] = Math.cos(angle);  // X 
            point1[1] = Math.sin(angle);  // Y 
    
            // Add noise
            point1[0] += random.nextGaussian() * noise;
            point1[1] += random.nextGaussian() * noise;
    
            data.add(point1);
            labels.add(0); 
        }
    
        // points second moon (lower half-circle)
        for (int i = 0; i < nSamples / 2; i++) {
            double[] point2 = new double[2];
            double angle = Math.PI * i / (nSamples / 2);  // Angle 
            point2[0] = Math.cos(angle) + 1;  // X coordinate (shifted horizontally to the right)
            point2[1] = -Math.sin(angle) + verticalShift;  // Y coordinate (shifted vertically by verticalShift)
    
            // Add noise
            point2[0] += random.nextGaussian() * noise;
            point2[1] += random.nextGaussian() * noise;
    
            data.add(point2);
            labels.add(1); 
        }
    
        return new Pair<>(data, labels);  
    }

    /**
     * Generates a 2D dataset with two concentric circles.
     * <p>
     * The circles are created with a specified scaling factor to control their distance.
     * </p>
     *
     * @param nSamples The total number of data points to generate.
     * @param noise The noise level added to the data points.
     * @param factor The scaling factor for the second circle.
     * @return A {@link Pair} containing the generated data points and their corresponding labels.
     */

    public Pair<List<double[]>, List<Integer>> makeCircles(int nSamples, double noise, double factor) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        for (int i = 0; i < nSamples / 2; i++) {
            double[] point1 = new double[2];
            double angle1 = 2 * Math.PI * i / (nSamples / 2);
            point1[0] = Math.cos(angle1); // X first circle
            point1[1] = Math.sin(angle1); // Y first circle
            
            double[] point2 = new double[2];
            double angle2 = 2 * Math.PI * (i + nSamples / 2) / (nSamples / 2);
            point2[0] = factor * Math.cos(angle2); // X second circle (scaled by factor)
            point2[1] = factor * Math.sin(angle2); // Y second circle
            
            // Add noise
            point1[0] += random.nextGaussian() * noise;
            point1[1] += random.nextGaussian() * noise;
            point2[0] += random.nextGaussian() * noise;
            point2[1] += random.nextGaussian() * noise;

            data.add(point1);
            labels.add(0); // Label for the first circle
            data.add(point2);
            labels.add(1); // Label for the second circle
        }

        return new Pair<>(data, labels);  
    }
}
