package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class LinearRegressionHypothesis implements Hypothesis{

    public Matrix guess(Matrix theta, Matrix x) {
        return theta.transpose().multiply(x.transpose());
    }

}
