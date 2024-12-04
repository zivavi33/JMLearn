package net.acimon.jmlearn.datasets;

import net.acimon.jmlearn.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SyntheticDataGenerator {

    private Random random;

    public SyntheticDataGenerator() {
        this.random = new Random();
    }

    // Generates a 2D dataset with well-separated blobs
    public Pair<List<double[]>, List<Integer>> makeBlobs(int nSamples, int nClusters, double clusterStd, int nDimensions) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        List<double[]> centroids = new ArrayList<>();
        
        // Randomly generate the centers of the clusters
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
            
            // Generate random data point around the centroid
            for (int j = 0; j < nDimensions; j++) {
                point[j] = centroid[j] + random.nextGaussian() * clusterStd;
            }
            data.add(point);
            labels.add(clusterIndex);  // Assign label based on the cluster
        }

        return new Pair<>(data, labels);  // Return Pair of data and labels
    }

    // Generates a 2D dataset of interleaving half circles (moons)
    public Pair<List<double[]>, List<Integer>> makeMoons(int nSamples, double noise) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
    
        // Define the vertical distance between the centers of the two moons
        double verticalShift = 0.25;  // Set the desired vertical distance between the moons (e.g., 0.5)
    
        // Generate points for the first moon (upper half-circle)
        for (int i = 0; i < nSamples / 2; i++) {
            double[] point1 = new double[2];
            double angle = Math.PI * i / (nSamples / 2);  // Angle for the first half-moon
            point1[0] = Math.cos(angle);  // X coordinate
            point1[1] = Math.sin(angle);  // Y coordinate
    
            // Add noise
            point1[0] += random.nextGaussian() * noise;
            point1[1] += random.nextGaussian() * noise;
    
            data.add(point1);
            labels.add(0); // Label for the first moon
        }
    
        // Generate points for the second moon (lower half-circle)
        for (int i = 0; i < nSamples / 2; i++) {
            double[] point2 = new double[2];
            double angle = Math.PI * i / (nSamples / 2);  // Angle for the second half-moon
            point2[0] = Math.cos(angle) + 1;  // X coordinate (shifted horizontally to the right)
            point2[1] = -Math.sin(angle) + verticalShift;  // Y coordinate (shifted vertically by verticalShift)
    
            // Add noise
            point2[0] += random.nextGaussian() * noise;
            point2[1] += random.nextGaussian() * noise;
    
            data.add(point2);
            labels.add(1); // Label for the second moon
        }
    
        return new Pair<>(data, labels);  // Return Pair of data and labels
    }

    // Generates a 2D dataset with two concentric circles
    public Pair<List<double[]>, List<Integer>> makeCircles(int nSamples, double noise, double factor) {
        List<double[]> data = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        for (int i = 0; i < nSamples / 2; i++) {
            double[] point1 = new double[2];
            double angle1 = 2 * Math.PI * i / (nSamples / 2);
            point1[0] = Math.cos(angle1); // X coordinate for the first circle
            point1[1] = Math.sin(angle1); // Y coordinate for the first circle
            
            double[] point2 = new double[2];
            double angle2 = 2 * Math.PI * (i + nSamples / 2) / (nSamples / 2);
            point2[0] = factor * Math.cos(angle2); // X coordinate for the second circle (scaled by factor)
            point2[1] = factor * Math.sin(angle2); // Y coordinate for the second circle
            
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

        return new Pair<>(data, labels);  // Return Pair of data and labels
    }
}
