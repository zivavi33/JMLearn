package net.acimon.jmlearn.datasets;

import net.acimon.jmlearn.utils.CSVLoader;

import java.io.InputStream;
import java.util.List;
/**
 * The DataSetLoader class provides utilities to load datasets for machine learning tasks.
 * <p>
 * This class includes methods to load dataset files from the resources folder, parse them into usable formats, and print their contents for debugging purposes.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Load datasets from the `resources/datasets` folder using the {@code loadDataset} method.</li>
 *     <li>Parse CSV datasets into a list of object arrays using the {@code loadCSVData} method.</li>
 *     <li>Debug and visualize dataset contents using the {@code printDataset} method.</li>
 *     <li>Handles CSV datasets with or without headers.</li>
 * </ul>
 *
 * @see CSVLoader for parsing CSV files and printing dataset contents.
 */

public class DataSetLoader {

    /**
     * Load a dataset from the resources folder as an InputStream.
     */
    public static InputStream loadDataset(String datasetName) {
        return DataSetLoader.class.getClassLoader().getResourceAsStream("datasets/" + datasetName + ".csv");
    }

    /**
     * Loads the content of a CSV dataset into a {@code List} of {@code Object[]} arrays.
     * <p>
     * This method parses the content of the specified dataset file, converting each row into an object array.
     * It uses the {@link CSVLoader} utility to handle CSV parsing.
     * </p>
     *
     * @param datasetName The name of the dataset file (without the `.csv` extension).
     * @param hasHeaders  A boolean indicating whether the CSV file contains headers.
     * @return A {@code List} of {@code Object[]} arrays representing the dataset.
     * @throws IllegalArgumentException if the dataset cannot be found or an error occurs during parsing.
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
     * Prints the contents of a loaded dataset for debugging purposes.
     * <p>
     * This method loads the specified dataset, parses it into a {@code List} of {@code Object[]} arrays, 
     * and prints the data to the console using the {@link CSVLoader}'s print functionality.
     * </p>
     *
     * @param datasetName The name of the dataset file (without the `.csv` extension).
     * @param hasHeaders  A boolean indicating whether the CSV file contains headers.
     * @throws IllegalArgumentException if the dataset file cannot be found or an error occurs during parsing.
     */
    public static void printDataset(String datasetName, boolean hasHeaders) {
        List<Object[]> data = loadCSVData(datasetName, hasHeaders);
        new CSVLoader(hasHeaders).printData(data);
    }
}
