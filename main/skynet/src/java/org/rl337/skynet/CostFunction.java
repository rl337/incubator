package org.rl337.skynet;

import org.rl337.skynet.costfunctions.DifferenceSquareCostFunction;
import org.rl337.skynet.types.Matrix;

public interface CostFunction {
    Matrix cost(Hypothesis h, Matrix theta, Matrix trials, Matrix observations);
    
    public static final CostFunction DifferenceSquare = new DifferenceSquareCostFunction();
}
