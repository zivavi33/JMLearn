

package net.acimon.jmlearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.acimon.jmlearn.utils.Pair;
import net.acimon.jmlearn.utils.TypeCaster;
import net.acimon.jmlearn.utils.CSVLoader;
import net.acimon.jmlearn.datasets.DataSetLoader;
import net.acimon.jmlearn.datasets.SyntheticDataGenerator;
import net.acimon.jmlearn.utils.DatasetSplitter;
import net.acimon.jmlearn.models.ensemble.Bagging;
import net.acimon.jmlearn.models.neighbors.KNN;
import net.acimon.jmlearn.models.tree.DecisionTree;
import net.acimon.jmlearn.models.ensemble.RandomForestTree;
import net.acimon.jmlearn.utils.PlotData;
import net.acimon.jmlearn.datasets.SyntheticDataGenerator;

public class Main {
    // public static void main(String[] args) {
    //     // Create an instance of SyntheticDataGenerator
    //     SyntheticDataGenerator dataGenerator = new SyntheticDataGenerator();

    //     // Generate and plot blobs (2D data, 3 clusters, standard deviation 1.0)
    //     Pair<List<double[]>, List<Integer>> blobsPair = dataGenerator.makeBlobs(300, 4, 0.5, 2); // 2D data, 3 clusters
    //     List<double[]> blobsData = blobsPair.first;
    //     List<Integer> blobsLabels = blobsPair.second;

    //     PlotData.plotData(blobsData, "Generated Blobs", blobsLabels);

    //     // Generate and plot moons (2D data, some noise)
    //     Pair<List<double[]>, List<Integer>> moonsPair = dataGenerator.makeMoons(300, 0.1); // 2D data, some noise
    //     List<double[]> moonsData = moonsPair.first;
    //     List<Integer> moonsLabels = moonsPair.second;

    //     PlotData.plotData(moonsData, "Generated Moons", moonsLabels);

    //     // Generate and plot circles (2D data, noise, concentric circles)
    //     Pair<List<double[]>, List<Integer>> circlesPair = dataGenerator.makeCircles(300, 0.1, 0.5); // 2D data, noise, concentric circles
    //     List<double[]> circlesData = circlesPair.first;
    //     List<Integer> circlesLabels = circlesPair.second;

    //     PlotData.plotData(circlesData, "Generated Circles", circlesLabels);
    // }


     public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        
        // OPTION 1: load dataset from local csv
        // CSVLoader loader = new CSVLoader(true); 
        // List<Object[]> rows = loader.loadCSV("src/main/resources/datasets/iris_dataset.csv");

        // OPTION 2: load "build-in" dataset (iris for example)
        List<Object[]> rows = DataSetLoader.loadCSVData("breast_cancer", true);

        List<List<Double>> trainRows = new ArrayList<>();
        List<Integer> labelRows = new ArrayList<>();

        // Iterate over the rows
        for (Object[] row : rows) {
            // Convert first 4 elements to doubles for trainRows
            List<Double> trainRow = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                trainRow.add(Double.parseDouble(row[i].toString()));
            }
            trainRows.add(trainRow);

            // Convert the 5th element (label) to integer for labelRows
            TypeCaster typeCaster = new TypeCaster();
            int value = typeCaster.castToInt(row[30].toString());
            labelRows.add(value);
        }

        // Convert List<List<Double>> to double[][]
        double[][] trainData = new double[trainRows.size()][30];
        for (int i = 0; i < trainRows.size(); i++) {
            for (int j = 0; j < 30; j++) {
                trainData[i][j] = trainRows.get(i).get(j);
            }
        }

        // Convert List<Integer> to int[]
        int[] labelData = new int[labelRows.size()];
        for (int i = 0; i < labelRows.size(); i++) {
            labelData[i] = labelRows.get(i);
        }

        System.out.println("Train Rows: " + trainData.length);
        System.out.println("Label Rows: " + labelData.length);

        // Split the dataset
        DatasetSplitter datasetSplitter = new DatasetSplitter(0.25, 23196L);
        DatasetSplitter.SplitResult result = datasetSplitter.split(trainData, labelData);

        // Optionally print the split result to verify
        System.out.println("Train data size: " + result.getTrainFeatures().length);
        System.out.println("Test data size: " + result.getValFeatures().length);

        // Set up the base model (e.g., KNN)
        // KNN knn = new KNN(5, "euclidean");
        // DecisionTree model = new DecisionTree(10);
        RandomForestTree model = new RandomForestTree(10, 10, 0.7);

        // Create the Bagging model using the base KNN model
        // Bagging bagging = new Bagging(model, 10, 0.8, 42, 10); // 10 bags, 80% sample size, 4 workers

        // Train the Bagging model
        model.fit(result.getTrainFeatures(), result.getTrainLabels());

        // Predict using the Bagging model
        int[] results_clf = model.predict(result.getValFeatures());

        // Print predictions and accuracy
        for (int i = 0; i < results_clf.length; i++) {
            System.out.println("Predicted label: " + results_clf[i] + ", Actual label: " + result.getValLabels()[i]);
        }
        System.out.println("Accuracy: " + DecisionTree.accuracy(result.getValLabels(), results_clf));
    }
}
// package net.acimon.jmlearn;

// import net.acimon.jmlearn.datasets.SyntheticDataGenerator;
// import net.acimon.jmlearn.models.cluster.KMeans;
// import net.acimon.jmlearn.utils.Pair;
// import net.acimon.jmlearn.utils.PlotData;
// import net.acimon.jmlearn.metrics.Classification;


// import java.util.List;

// public class Main {
//     public static void main(String[] args) {
//         // // Generate synthetic data using makeBlobs
//         // SyntheticDataGenerator dataGenerator = new SyntheticDataGenerator();
//         // int nSamples = 1000;    // Total number of samples
//         // int nClusters = 5;     // Number of clusters (blobs)
//         // double clusterStd = 1.0; // Standard deviation of blobs
//         // int nDimensions = 2;   // Data points in 2D
        
//         // // Generate data and labels
//         // Pair<List<double[]>, List<Integer>> dataPair = dataGenerator.makeBlobs(nSamples, nClusters, clusterStd, nDimensions);
//         // List<double[]> data = dataPair.first;  // Data points

//         // // Convert the data list to a 2D array for KMeans
//         // double[][] X = new double[nSamples][nDimensions];
//         // for (int i = 0; i < nSamples; i++) {
//         //     X[i] = data.get(i);
//         // }

//         // // Initialize KMeans with the desired number of clusters and iterations
//         // int k = 5; // Number of clusters
//         // int maxIter = 100; // Maximum number of iterations
//         // KMeans kMeans = new KMeans(k, maxIter,true);
//         // kMeans.fit(X); // Fit the KMeans algorithm

//         int[] yTrue = {0, 1, 2, 0, 2, 2, 3};
//         int[] yPred = {0, 1, 1, 0, 2, 2, 1};
//         Classification classification = new Classification(yTrue, yPred, 4); // 3 classes
//         double accuracy = classification.accuracy();
//         String summary = classification.summary("weighted");
//         System.out.println(summary);

//     }
        

//     }

