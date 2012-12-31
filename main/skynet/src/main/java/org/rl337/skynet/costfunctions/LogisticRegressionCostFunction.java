package org.rl337.skynet.costfunctions;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Log;
import org.rl337.math.types.Matrix;

public class LogisticRegressionCostFunction extends AbstractRegularizedCostFunction {

    public LogisticRegressionCostFunction(double lambda) {
        super(lambda);
    }

    public Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        
        double lambda = getLambda();
        
        Matrix hx = h.guess(theta, x);
        Matrix ones = Matrix.ones(y.getRows(), y.getColumns());
        
        Matrix term1 = y.multiplyElementWise(Log.RealLog.evaluate(hx));
        Matrix term2 = ones.subtract(y).multiplyElementWise(Log.RealLog.evaluate(ones.subtract(hx)));
        Matrix whole = term1.add(term2);
        
        int m = y.getRows();
        double regularization = lambda / (2 * m) * (theta.sliceRows(1, theta.getRows() - 1).sum());
        return whole.divide(m).sumRows().multiply(-1).add(regularization);
    }

    public Matrix gradient(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        double lambda = getLambda();
        
        Matrix hx = h.guess(theta, x);
        Matrix deltas = hx.subtract(y);
        int m = y.getRows();
        Matrix gradient = deltas.transpose().multiply(x).divide(m).transpose();
        
        double regularization = lambda / m;
        return gradient.add(theta.add(regularization));
    }

}
