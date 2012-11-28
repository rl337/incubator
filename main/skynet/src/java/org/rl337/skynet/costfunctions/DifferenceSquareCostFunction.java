package org.rl337.skynet.costfunctions;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class DifferenceSquareCostFunction extends AbstractRegularizedCostFunction {

    public DifferenceSquareCostFunction(double lambda) {
        super(lambda);
    }

    public Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        double lambda = getLambda();
        Matrix hx = h.guess(theta, x);
        Matrix error = hx.subtract(y);
        Matrix errorSq = error.multiplyElementWise(error);
        Matrix regularization = theta.pow(2).multiply(lambda).sumRows();
        
        int m = y.getRows();
        return errorSq.divide(2 * m).sumRows().add(regularization);
    }
    
    public Matrix gradient(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        double lambda = getLambda();
        int m = y.getRows();
        Matrix hx = h.guess(theta, x);
        Matrix deltas = hx.subtract(y);
        Matrix regularization = theta.multiply(lambda / m);
        regularization.setValue(0, 0, 0);
        Matrix gradient = deltas.transpose().multiply(x).divide(m).transpose().add(regularization);
        return gradient;
    }
}
