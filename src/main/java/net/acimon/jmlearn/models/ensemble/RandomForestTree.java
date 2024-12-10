package net.acimon.jmlearn.models.ensemble;
import net.acimon.jmlearn.models.Model;
import net.acimon.jmlearn.models.tree.DecisionTree;

import java.util.logging.Logger;


public class RandomForestTree implements Model{

    private Bagging _bootStrpAgg;
    private DecisionTree _baseTree;
    private int _treeNumber;
    private Integer _seed;
    private double[][] _X_train;
    private int[] _Y_train;

    private static final Logger logger = Logger.getLogger(Bagging.class.getName());

    public RandomForestTree(int treeNumber,int minSamplesSplit, int maxDepth, int nFeatures, Integer seed, double sampleSize){
        this._treeNumber = treeNumber;
        this._baseTree = new DecisionTree(minSamplesSplit, maxDepth, nFeatures);
        this._bootStrpAgg = new Bagging(_baseTree, treeNumber, sampleSize, _seed, treeNumber);
    }
    public RandomForestTree(int treeNumber, int maxDepth, double sampleSize){
        this._treeNumber = treeNumber;
        this._baseTree = new DecisionTree(maxDepth);
        this._bootStrpAgg = new Bagging(_baseTree, treeNumber, sampleSize, _seed, treeNumber);
    }
    public RandomForestTree(RandomForestTree other){
        this._treeNumber = other._treeNumber;
        this._baseTree = new DecisionTree(other._baseTree.getMinSamplesSplit(),
            other._baseTree.getMaxDepth(),
            other._baseTree.getNFeatures());
       this._bootStrpAgg = new Bagging(this._baseTree, other._bootStrpAgg.getBagsNumber(),
            other._bootStrpAgg.getSampleSize(),
            other._bootStrpAgg.getSeed(),
            other._bootStrpAgg.getBagsNumber());
    }
    @Override
    public Model clone(){
        return new RandomForestTree(this);
    }
    @Override
    public void fit(double[][] X, int[] Y){
        if (X.length == 0 || Y.length == 0) {
            throw new IllegalArgumentException("Training data cannot be empty.");
        }
        if (X.length != Y.length) {
            throw new IllegalArgumentException("The number of samples in X and Y must match.");
        }
        int numFeatures = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != numFeatures) {
                throw new IllegalArgumentException("All feature vectors must have the same number of dimensions.");
            }
        }
        this._X_train = X;
        this._Y_train = Y;
        _bootStrpAgg.fit(_X_train, _Y_train);
    }
    @Override
    public void fit(double[][] _X_train){
        logger.severe("fit method without labels not supported for bootStrpAgg model (randomForest)");
        throw new UnsupportedOperationException("fit method without labels not supported for bootStrpAgg model(randomForest)");
    }

    @Override
    public int[] predict(double[][] X){
        if (X.length == 0) {
            throw new IllegalArgumentException("Test data cannot be empty.");
        }
        int numFeatures = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length!= numFeatures) {
                throw new IllegalArgumentException("All feature vectors must have the same number of dimensions.");
            }
        }
        int[] predictions = _bootStrpAgg.predict(X);
        return predictions;
    }
}