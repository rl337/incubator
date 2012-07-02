package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public class GradientDescentOptimizer extends Optimizer {
    private static final int smDebugSampleSize = 100;
    private double mAlpha;
    private boolean mDebug;
    private Matrix mDebugData;

    public GradientDescentOptimizer(double alpha, Hypothesis h, CostFunction c, boolean debug) {
        super(h, c);
        mDebug = debug;
        mAlpha = alpha;
        mDebugData = null;
    }
    
    public GradientDescentOptimizer(double alpha, Hypothesis h, CostFunction c) {
        this(alpha, h, c, false);
    }

    public Matrix run(Matrix theta, Matrix x, Matrix y, int maxIterations, double epsilon) {
        int m = x.getRows();
        Matrix result = theta;
        
        Hypothesis h = getHypothesis();
        CostFunction c = getCostFunction();
        double lastScore = 0;
        double[][] debugValues = null;
        if (mDebug) {
            int debugSize = maxIterations / smDebugSampleSize;
            debugValues = new double[debugSize][2];
        }
        
        int i;
        for(i = 0; i < maxIterations; i++) {
            // Calculates Gradient
            Matrix gradient = c.gradient(h, result, x, y);
            
           // Performs iteration
            result = result.subtract(gradient.multiply(mAlpha / m));
            
            // In theory, score should be decreasing over time...
            // check to see if delta score is less than epsilon.
            // if it is, we've converged enough.
            double score = result.sum();
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
            }
        }
        
        if (mDebug) {
            mDebugData = Matrix.matrix(debugValues, 0, i / smDebugSampleSize);
        }
        
        return result;
    }
    
    public Matrix getDebugData() {
        return mDebugData;
    }
    
}
