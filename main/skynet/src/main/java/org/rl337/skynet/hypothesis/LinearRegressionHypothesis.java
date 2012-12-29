package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

public class LinearRegressionHypothesis implements Hypothesis{

    public Matrix guess(Matrix theta, Matrix x) {
        // (theta' * x')' is the same as x * theta
        return x.multiply(theta);
    }

}
