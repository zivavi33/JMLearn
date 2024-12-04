package net.acimon.jmlearn.datasets;

import net.acimon.jmlearn.utils.CSVLoader;

import java.io.InputStream;
import java.util.List;

public class DataSetLoader {

    /**
     * Load a dataset from the resources folder as an InputStream.
     */
    public static InputStream loadDataset(String datasetName) {
        return DataSetLoader.class.getClassLoader().getResourceAsStream("datasets/" + datasetName + ".csv");
    }

    /**
     * Load dataset content into a List of Object arrays.
     */
    public static List<Object[]> loadCSVData(String datasetName, boolean hasHeaders) {
        CSVLoader csvLoader = new CSVLoader(hasHeaders);
        try (InputStream inputStream = loadDataset(datasetName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Dataset not found: " + datasetName);
            }
            return csvLoader.loadCSV(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Dataset not found: " + datasetName, e);
        }
    }

    /**
     * Utility to print loaded dataset for debugging.
     */
    public static void printDataset(String datasetName, boolean hasHeaders) {
        List<Object[]> data = loadCSVData(datasetName, hasHeaders);
        new CSVLoader(hasHeaders).printData(data);
    }
}
