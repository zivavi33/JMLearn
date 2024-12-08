package net.acimon.jmlearn.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PlotData {

    public static void plotData(List<double[]> data, String title, List<Integer> labels) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Map to hold series for each unique label
        Map<Integer, XYSeries> seriesMap = new HashMap<>();
        Set<Integer> uniqueLabels = new HashSet<>(labels);

        // Create a series for each unique label
        for (int label : uniqueLabels) {
            seriesMap.put(label, new XYSeries("Class " + label));
        }

        // Add data points to the appropriate series
        for (int i = 0; i < data.size(); i++) {
            double[] point = data.get(i);
            int label = labels.get(i);
            seriesMap.get(label).add(point[0], point[1]);
        }

        // Add all series to the dataset
        for (XYSeries series : seriesMap.values()) {
            dataset.addSeries(series);
        }

        // Create a chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,           
                "X",             
                "Y",             
                dataset,         
                PlotOrientation.VERTICAL,
                true,            
                true,            
                false          
        );

        // Customize colors for each class
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        int seriesIndex = 0;

        // Generate colors using a gradient or palette
        Color[] colors = generateColors(uniqueLabels.size());
        for (int label : uniqueLabels) {
            chart.getXYPlot().getRenderer().setSeriesPaint(seriesIndex, colors[seriesIndex]);
            seriesIndex++;
        }

        // Display the chart in a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Show the chart in a JFrame
        JFrame frame = new JFrame("Synthetic Data Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // Helper method to generate distinct colors for each class
    private static Color[] generateColors(int numColors) {
        Color[] colors = new Color[numColors];
        for (int i = 0; i < numColors; i++) {
            float hue = (float) i / numColors; // Generate hues equally spaced
            colors[i] = Color.getHSBColor(hue, 0.8f, 0.8f); // Use high saturation and brightness
        }
        return colors;
    }
    public static void plotDataWithCentroids(List<double[]> data, List<Integer> labels, double[][] centroids, String title, JFrame frame, ChartPanel chartPanel) {
        XYSeriesCollection dataset = new XYSeriesCollection();
    
        // Map to hold series for each unique label
        Map<Integer, XYSeries> seriesMap = new HashMap<>();
        Set<Integer> uniqueLabels = new HashSet<>(labels);
    
        // Create a series for each unique label
        for (int label : uniqueLabels) {
            seriesMap.put(label, new XYSeries("Class " + label));
        }
    
        // Add data points to the appropriate series
        for (int i = 0; i < data.size(); i++) {
            double[] point = data.get(i);
            int label = labels.get(i);
            seriesMap.get(label).add(point[0], point[1]);
        }
    
        // Add all series to the dataset
        for (XYSeries series : seriesMap.values()) {
            dataset.addSeries(series);
        }
    
        // Create a chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    
        // Plot centroids as distinct points
        XYSeries centroidsSeries = new XYSeries("Centroids");
        for (int i = 0; i < centroids.length; i++) {
            centroidsSeries.add(centroids[i][0], centroids[i][1]);
        }
        dataset.addSeries(centroidsSeries);
        
    
        // Customize colors for each class
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        int seriesIndex = 0;
    
        // Generate colors using a gradient or palette
        Color[] colors = generateColors(uniqueLabels.size());
        for (int label : uniqueLabels) {
            chart.getXYPlot().getRenderer().setSeriesPaint(seriesIndex, colors[seriesIndex]);
            seriesIndex++;
        }
    
        // Customize centroids
        chart.getXYPlot().getRenderer().setSeriesPaint(seriesIndex, Color.BLACK); // Set centroids color
        chart.getXYPlot().getRenderer().setSeriesShape(seriesIndex, ShapeUtils.createDiamond(10)); // Set the marker size
    
        // Update the chart panel with the new chart
        chartPanel.setChart(chart);
        chartPanel.revalidate();
        chartPanel.repaint();
    
        // Make sure the frame is visible and properly updated
        frame.pack();
        frame.setVisible(true);
    }
    

    
}
