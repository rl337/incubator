package org.rl337.skynet;

import org.rl337.math.types.Matrix;

public abstract class Optimizer {
    public static double DEFAULT_EPSILON = 1.0E-20;
    
    private Hypothesis mHypothesis;
    private CostFunction mCostFunction;
    
    protected Optimizer(Hypothesis h, CostFunction c) {
        mHypothesis = h;
        mCostFunction = c;
    }
    
    public abstract Matrix run(Matrix initialTheta, DataSet trainingData, DataSet labelData);

    public Hypothesis getHypothesis() {
        return mHypothesis;
    }
    
    public CostFunction getCostFunction() {
        return mCostFunction;
    }

}
