package org.rl337.skynet.costfunctions;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;

public class LogisticRegressionCostFunction implements CostFunction {

    public Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        
        Matrix hx = h.guess(theta, x);
        Matrix ones = Matrix.ones(y.getRows(), y.getColumns());
        
        Matrix term1 = y.multiplyElementWise(Log.RealLog.evaluate(hx));
        Matrix term2 = ones.subtract(y).multiplyElementWise(Log.RealLog.evaluate(ones.subtract(hx)));
        Matrix whole = term1.add(term2);
        
        int m = y.getRows();
        return whole.divide(m).sumRows().multiply(-1);
    }

}
