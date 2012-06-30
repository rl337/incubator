package org.rl337.skynet;

import org.rl337.skynet.costfunctions.DifferenceSquareCostFunction;
import org.rl337.skynet.costfunctions.LogisticRegressionCostFunction;
import org.rl337.skynet.types.Matrix;

public interface CostFunction {
    Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y);
    
    public static final CostFunction DifferenceSquare = new DifferenceSquareCostFunction();
    public static final CostFunction LogisticRegression = new LogisticRegressionCostFunction();
}
