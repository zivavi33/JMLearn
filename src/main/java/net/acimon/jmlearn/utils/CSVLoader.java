package net.acimon.jmlearn.utils;

import java.io.*;
import java.util.*;

/**
 * The CSVLoader class provides methods to load and parse CSV files.
 * <p>
 * This class supports loading CSV data from a file or an input stream, with optional handling of headers. 
 * It converts each row of data to an array of Objects, with the ability to handle both numeric and string data types.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Load CSV data from a file path or an input stream using {@code loadCSV} methods.</li>
 *     <li>Parse the CSV content into a list of {@code Object[]} where each array contains values from a row.</li>
 *     <li>Supports optional headers, saving the header values in an array if the CSV has headers.</li>
 *     <li>Handles both string and numeric values by attempting to parse numeric values into {@code Double}.</li>
 *     <li>Print the loaded data for debugging using {@code printData}.</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * CSVLoader loader = new CSVLoader(true); // true if headers are expected
 * List<Object[]> data = loader.loadCSV("path/to/csvfile.csv");
 * loader.printData(data);
 * </pre>
 *
 * <h2>Constructor Summary</h2>
 * <ul>
 *     <li>{@link CSVLoader#CSVLoader(boolean)} - Constructs a CSVLoader with the option to specify whether the CSV has headers.</li>
 * </ul>
 * 
 * <h2>Getter Summary</h2>
 * <ul>
 *     <li>{@link CSVLoader#getHeaders} - returns the headers.</li>
 * </ul>
 */

public class CSVLoader {

    private boolean headers;           
    private String[] headerArray;      

    public CSVLoader(boolean headers) {
        this.headers = headers;
    }

    /**
     * Loads a CSV file from a specified file path and returns its data.
     * <p>
     * This method reads the CSV file line by line and parses each row. The first row is treated as headers 
     * if the {@code headers} flag is set to true.
     * </p>
     *
     * @param filePath The path to the CSV file to load.
     * @return A list of {@code Object[]} representing the data from the CSV file.
     * @throws IOException If an error occurs while reading the file.
     */
    public List<Object[]> loadCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return parseCSV(br);
        }
    }

    /**
     * Loads a CSV file from an input stream and returns its data.
     * <p>
     * This method reads the CSV content from an input stream and parses each row. The first row is treated as headers 
     * if the {@code headers} flag is set to true.
     * </p>
     *
     * @param inputStream The input stream from which to load the CSV data.
     * @return A list of {@code Object[]} representing the data from the CSV file.
     * @throws IOException If an error occurs while reading the input stream.
     */
    public List<Object[]> loadCSV(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseCSV(br);
        }
    }

    /**
     * Parses the CSV content from a {@code BufferedReader}.
     * <p>
     * This method processes the lines from the CSV file, converting each row into an array of objects. 
     * The first row is treated as headers if the {@code headers} flag is set to true.
     * </p>
     *
     * @param br The {@code BufferedReader} from which the CSV content is read.
     * @return A list of {@code Object[]} representing the parsed data.
     * @throws IOException If an error occurs while reading the CSV content.
     */
    private List<Object[]> parseCSV(BufferedReader br) throws IOException {
        List<Object[]> rows = new ArrayList<>();
        String line;

        if ((line = br.readLine()) != null) {
            String[] firstRow = line.split(",");

            if (headers) {
                // Save the headers 
                this.headerArray = firstRow;
            } else {
                // If no headers, treat the first row as data
                rows.add(convertRowToData(firstRow));
            }
        }

        //remaining rows
        while ((line = br.readLine()) != null) {
            rows.add(convertRowToData(line.split(",")));
        }

        return rows;
    }

    /**
     * Converts a row (array of strings) to an array of objects (either String or Double).
     * <p>
     * This method attempts to parse each value in the row as a double. If it fails, it keeps the value as a string.
     * </p>
     *
     * @param row The array of strings to convert.
     * @return An array of {@code Object} representing the converted values.
     */
    private Object[] convertRowToData(String[] row) {
        Object[] convertedRow = new Object[row.length];
        for (int i = 0; i < row.length; i++) {
            String value = row[i];
            try {
                // Try to parse the value as a double
                convertedRow[i] = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // If it can't be parsed, leave it as a String
                convertedRow[i] = value;
            }
        }
        return convertedRow;
    }

    public String[] getHeaders() {
        return headerArray;
    }

    /**
     * Utility method to print the loaded data to the console for debugging.
     * <p>
     * This method prints each row of data, checking its type and formatting it accordingly.
     * </p>
     *
     * @param data The loaded CSV data to print.
     */

    public void printData(List<Object[]> data) {
        if (headerArray != null) {
            System.out.println("Headers: " + String.join(", ", headerArray));
        }
        for (Object[] row : data) {
            
            for (Object val : row) {
                if (val instanceof Double) {
                    System.out.print(val + "\t");
                } else {
                    System.out.print("\"" + val + "\"\t");
                }
            }
            System.out.println();
        }
    }
}
