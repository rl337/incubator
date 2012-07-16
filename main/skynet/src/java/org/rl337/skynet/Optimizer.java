package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public abstract class Optimizer {
    public static double DEFAULT_EPSILON = 1.0E-20;
    
    private Hypothesis mHypothesis;
    private CostFunction mCostFunction;
    
    protected Optimizer(Hypothesis h, CostFunction c) {
        mHypothesis = h;
        mCostFunction = c;
    }
    
    public abstract Matrix run(Matrix theta, Matrix x, Matrix y, int maxIteration, double epsilon);
    
    public Matrix run(Matrix theta, Matrix x, Matrix y, int maxIteration) {
        return run(theta, x, y, maxIteration, DEFAULT_EPSILON);
    }

    public Hypothesis getHypothesis() {
        return mHypothesis;
    }
    
    public CostFunction getCostFunction() {
        return mCostFunction;
    }

}
