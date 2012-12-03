package org.rl337.skynet.costfunctions;

import org.rl337.skynet.CostFunction;

public abstract class AbstractRegularizedCostFunction implements CostFunction {
    private double mLambda;
    
    public AbstractRegularizedCostFunction(double lambda) {
        mLambda = lambda;
    }

    public double getLambda() {
        return mLambda;
    }

}
