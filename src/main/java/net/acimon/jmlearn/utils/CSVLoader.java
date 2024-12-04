package net.acimon.jmlearn.utils;

import java.io.*;
import java.util.*;

public class CSVLoader {

    private boolean headers;           // Whether the CSV is expected to have headers
    private String[] headerArray;      // Stores the headers if they exist

    public CSVLoader(boolean headers) {
        this.headers = headers;
    }
    public List<Object[]> loadCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return parseCSV(br);
        }
    }

    public List<Object[]> loadCSV(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseCSV(br);
        }
    }

    // public List<Object[]> loadCSV(String filePath) throws IOException {
    //     List<Object[]> rows = new ArrayList<>();
    //     try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
    //         String line;

    //         // Check the first line for headers if headers=true
    //         if ((line = br.readLine()) != null) {
    //             String[] firstRow = line.split(",");

    //             if (headers) {
    //                 // Save the headers and move to the next line
    //                 this.headerArray = firstRow;
    //             } else {
    //                 // If no headers, treat the first row as data
    //                 rows.add(convertRowToData(firstRow));
    //             }
    //         }

    //         // Process remaining rows
    //         while ((line = br.readLine()) != null) {
    //             rows.add(convertRowToData(line.split(",")));
    //         }
    //     }

    //     return rows;
    // }
    private List<Object[]> parseCSV(BufferedReader br) throws IOException {
        List<Object[]> rows = new ArrayList<>();
        String line;

        // Check the first line for headers if headers=true
        if ((line = br.readLine()) != null) {
            String[] firstRow = line.split(",");

            if (headers) {
                // Save the headers and move to the next line
                this.headerArray = firstRow;
            } else {
                // If no headers, treat the first row as data
                rows.add(convertRowToData(firstRow));
            }
        }

        // Process remaining rows
        while ((line = br.readLine()) != null) {
            rows.add(convertRowToData(line.split(",")));
        }

        return rows;
    }

    /**
     * Convert a row to an array of Objects (String or Double).
     * Attempts to convert each entry to a Double if it is a valid number.
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

    /**
     * Getter for headers array.
     */
    public String[] getHeaders() {
        return headerArray;
    }

    /**
     * Utility method to print the loaded data for debugging.
     */
    public void printData(List<Object[]> data) {
        if (headerArray != null) {
            System.out.println("Headers: " + String.join(", ", headerArray));
        }
        for (Object[] row : data) {
            // Print the row with each value, checking its type
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
