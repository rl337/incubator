package org.rl337.skynet.costfunctions;

import org.rl337.skynet.GradientCostFunction;

public abstract class AbstractRegularizedCostFunction implements GradientCostFunction {
    private double mLambda;
    
    public AbstractRegularizedCostFunction(double lambda) {
        mLambda = lambda;
    }

    public double getLambda() {
        return mLambda;
    }

}
