package net.acimon.jmlearn.models.ensemble;

import net.acimon.jmlearn.models.Model;
import net.acimon.jmlearn.utils.Pair;
import net.acimon.jmlearn.utils.Counter;
import net.acimon.jmlearn.models.ensemble.Bagging;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

import java.util.logging.Logger;


public class Bagging implements Model {
    
    public Model _model;
    private int _bagsNumber;
    private double _sampleSize;
    private final Integer _seed;
    private Random _random;
    public List<Model> _weakLearners;
    private int _numWorkers;
    private static final Logger logger = Logger.getLogger(Bagging.class.getName());

    



    public Bagging(Model model, int bagsNumber, double sampleSize, Integer seed, Integer numWorkers) {
        this._model = model;
        this._bagsNumber = bagsNumber;
        this._sampleSize = sampleSize;
        this._seed = seed;
        this._weakLearners = new ArrayList<>(); 
        this._random = (seed != null) ? new Random(seed) : new Random(); 
        this._numWorkers = (numWorkers != null) ? numWorkers:bagsNumber;
     
        
    }

    private Pair<double[][], int[]> bootSrtpAgg(double[][] _X_train, int[] _Y_train){
        int sampleSize = (int) Math.round(_X_train.length * _sampleSize);
        int dataSize = _X_train.length;
        int tempIdx;
        double[][] xBag = new double[sampleSize][];
        int[] yBag = new int[sampleSize];

        for (int i=0; i < (sampleSize); i++){//Iterate over the amount of sample in each "bag".
            tempIdx = this._random.nextInt(dataSize);
            xBag[i] = _X_train[tempIdx];
            yBag[i] = _Y_train[tempIdx];
        }
        return new Pair<>(xBag, yBag);
    } 


    private Model inFit(double[][] _X_train, int[] _Y_train){
        Pair<double[][], int[]> samplePair = bootSrtpAgg(_X_train, _Y_train);
        Model weakLearner = _model.clone();
        weakLearner.fit(samplePair.first, samplePair.second);
        return weakLearner;
    }
    @Override
    public void fit(double[][] X, int[] y) {
        ExecutorService executor = Executors.newFixedThreadPool(this._numWorkers);
        List<Future<Model>> futures = new ArrayList<>();
        
        // Submit tasks for weak learners
        for (int i = 0; i < _bagsNumber; i++) {
            final int finalI = i;
            futures.add(executor.submit(() -> {
                try {
                    Model weakLearner = inFit(X, y);
                    logger.info("Successfully trained weak learner " + finalI);  
                    return weakLearner;
                } catch (Exception e) {
                    logger.severe("Error during training weak learner " + finalI + ": " + e.getMessage());
                    return null;
                }
            }));
        }
    
        // Wait for all tasks to complete
        for (Future<Model> future : futures) {
            try {
                Model weakLearner = future.get();
                if (weakLearner != null) {
                    _weakLearners.add(weakLearner); // Only add non-null models
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.severe("Error when collecting future result: " + e.getMessage());
            }
        }
    
        executor.shutdown();
    
        // Check if weak learners are properly added
        if (_weakLearners.isEmpty()) {
            logger.severe("No weak learners were added!");
        }
    }
    
    @Override
    public void fit(double[][] _X_train){
        logger.severe("fit method without labels not supported for bootStrpAgg model");
        throw new UnsupportedOperationException("fit method without labels not supported for bootStrpAgg model");
    }


    @Override
    // Method to predict the label for a given input X
    public int[] predict(double[][] X) {
        // Get predictions from all weak learners (bags)
        int[][] weakPredictions = new int[X.length][_bagsNumber];  // Store predictions for each data point

        // Collect predictions from each bag (weak learner)
        for (int i = 0; i < _bagsNumber; i++) {
            Model weakModel = _weakLearners.get(i);
            int[] bagPredictions = weakModel.predict(X);  // Assume the Estimator's predict method returns an int[] for predictions
            for (int j = 0; j < X.length; j++) {
                weakPredictions[j][i] = bagPredictions[j];
            }
        }
        // Aggregate predictions by taking the most common label for each data point
        int[] finalPredictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            Counter counter  = new Counter(weakPredictions[i]);
            finalPredictions[i] = counter.mostCommon();
        }
        return finalPredictions;
    }

    @Override
    public Model clone() {
        logger.severe("clone method is not supported");
        throw new UnsupportedOperationException("clone method is not supported");
    }

    public Integer getSeed(){
        return this._seed;
    }
    
        

}
