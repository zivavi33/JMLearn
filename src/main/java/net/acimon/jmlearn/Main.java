package net.acimon.jmlearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.acimon.jmlearn.utils.TypeCaster;
import net.acimon.jmlearn.utils.CSVLoader;
import net.acimon.jmlearn.utils.DatasetSplitter;
import net.acimon.jmlearn.models.neighbors.KNN;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        CSVLoader loader = new CSVLoader(true); // Headers enabled
        List<Object[]> rows = loader.loadCSV("src/main/resources/datasets/iris_dataset.csv");

        List<List<Double>> trainRows = new ArrayList<>();
        List<Integer> labelRows = new ArrayList<>();

        // Iterate over the rows
        for (Object[] row : rows) {
            // Convert first 4 elements to doubles for trainRows
            List<Double> trainRow = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                trainRow.add(Double.parseDouble(row[i].toString()));
            }
            trainRows.add(trainRow);

            // Convert the 5th element (label) to double for labelRows
            // Double value = Double.parseDouble(row[4].toString());  // Parse the string as a Double
            // TypeCaster typeCaster = new TypeCaster("int");
            TypeCaster typeCaster = new TypeCaster();
            int value = typeCaster.castToInt(row[4].toString());
            labelRows.add(value);

            // if (value % 1 == 0) {
            //     // If it's a whole number, cast to integer
            //     labelRows.add((int) value.doubleValue()); // Cast directly to int
            // } else {
            //     // If not a whole number, keep it as a double or handle it differently
            //     labelRows.add(value.intValue()); // Optionally round if needed
            // }
        }

        // Convert List<List<Double>> to double[][]
        double[][] trainData = new double[trainRows.size()][4];
        for (int i = 0; i < trainRows.size(); i++) {
            for (int j = 0; j < 4; j++) {
                trainData[i][j] = trainRows.get(i).get(j);
            }
        }

        // Convert List<Double> to double[]
        int[] labelData = new int[labelRows.size()];
        for (int i = 0; i < labelRows.size(); i++) {
            labelData[i] = labelRows.get(i);
        }

        System.out.println("Train Rows: " + trainData.length);
        System.out.println("Label Rows: " + labelData.length);

        // Split the dataset
        DatasetSplitter datasetSplitter = new DatasetSplitter(0.2, 42L);
        DatasetSplitter.SplitResult result = datasetSplitter.split(trainData, labelData);

        // Optionally print the split result to verify
        System.out.println("Train data size: " + result.getTrainFeatures().length);
        System.out.println("Test data size: " + result.getValFeatures().length);
        KNN knn = new KNN(5,"euclidean");
        knn.fit(result.getTrainFeatures(), result.getTrainLabels());
        int[] results_clf = knn.predict(result.getValFeatures());
        for (int i = 0; i < results_clf.length; i++) {
            System.out.println("Predicted label: " + results_clf[i] + ", Actual label: " + result.getValLabels()[i]);
        }
        System.out.println("Accuracy: "+KNN.accuracy(result.getValLabels(), results_clf));
        }  
}
