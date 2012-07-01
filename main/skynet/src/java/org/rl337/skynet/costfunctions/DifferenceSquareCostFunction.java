package org.rl337.skynet.costfunctions;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class DifferenceSquareCostFunction implements CostFunction {

    public Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        Matrix hx = h.guess(theta, x);
        Matrix error = hx.subtract(y);
        Matrix errorSq = error.multiplyElementWise(error);
        
        int m = y.getRows();
        
        return errorSq.divide(2 * m).sumRows();
    }
    
    public Matrix gradient(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        Matrix hx = h.guess(theta, x);
        Matrix deltas = hx.subtract(y);
        Matrix gradient = deltas.transpose().multiply(x).transpose();
        return gradient;
    }
}
