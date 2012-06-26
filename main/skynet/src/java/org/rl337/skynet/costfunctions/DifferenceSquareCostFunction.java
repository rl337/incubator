package org.rl337.skynet.costfunctions;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class DifferenceSquareCostFunction implements CostFunction {

    public Matrix cost(Hypothesis h, Matrix theta, Matrix trials, Matrix observations) {
        Matrix hx = h.guess(theta, trials);
        Matrix error = hx.subtract(observations);
        Matrix errorSq = error.multiplyElementWise(error);
        
        int m = observations.getRows();
        
        return errorSq.divide(2 * m).sumRows();
    }
}
