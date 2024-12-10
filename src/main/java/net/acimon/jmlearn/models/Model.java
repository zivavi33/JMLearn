package net.acimon.jmlearn.models;

public interface Model {

    int[] predict(double[][] dataPoints);
    void fit(double[][] dataPoints); // unsupervised models without labels
    void fit(double[][] dataPoints, int[] labels); // For supervised models (and unsupervised with labels).
    Model clone(); 
}