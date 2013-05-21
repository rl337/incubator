package org.rl337.skynet.optimizers;

import org.rl337.skynet.DataSet;
import org.rl337.skynet.GradientCostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.math.types.Matrix;

public abstract class AbstractIterativeOptimizer extends Optimizer {
    private static final int smDebugSampleSize = 100;

    private int mIterations;
    private double mEpsilon;
    private boolean mDebug;
    private Matrix mDebugData;

    protected AbstractIterativeOptimizer(Hypothesis h, GradientCostFunction c, int iterations, double epsilon, boolean debug) {
        super(h, c);
        mIterations = iterations;
        mEpsilon = epsilon;
        mDebug = debug;
    }
    
    public int getIterations() {
        return mIterations;
    }
    
    public double getEpsilon() {
        return mEpsilon;
    }

    @Override
    public Matrix run(Matrix initialTheta, DataSet training, DataSet labels) {
        Hypothesis h = getHypothesis();
        GradientCostFunction c = (GradientCostFunction) getCostFunction();
        double lastScore = 0;
        double[][] debugValues = null;
        if (mDebug) {
            int debugSize = mIterations / smDebugSampleSize;
            debugValues = new double[debugSize][2];
        }
        
        double epsilon = getEpsilon();
        Matrix x = training.getAll();
        Matrix y = labels.getAll();
        
        Matrix result = initialTheta;
        int debugSize = 0;
        for(int i = 0; i < mIterations; i++) {
           // Performs iteration
            result = runIteration(result, x, y);
            
            // In theory, score should be decreasing over time...
            // check to see if delta score is less than epsilon.
            // if it is, we've converged enough.
            double score = c.cost(h, result, x, y).sum();
            double deltaScore = lastScore - score;
            double error = deltaScore * deltaScore;
            if (error < epsilon) {
                break;
            }
            
            lastScore = score;
            if (mDebug && i % smDebugSampleSize == 0) {
                int index = i / smDebugSampleSize;
                debugValues[index][0] = i;
                debugValues[index][1] = error;
                debugSize++;
            }
        }
        
        if (mDebug && debugSize > 0) {
            mDebugData = Matrix.matrix(debugValues, 1, debugSize - 1);
        }
        
        return result;
    }
    
    
    public Matrix getDebugData() {
        return mDebugData;
    }
    
    public abstract Matrix runIteration(Matrix theta, Matrix x, Matrix y);

}
