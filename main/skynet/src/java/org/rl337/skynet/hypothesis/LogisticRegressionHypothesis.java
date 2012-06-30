package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Sigmoid;

public class LogisticRegressionHypothesis implements Hypothesis {

    public Matrix guess(Matrix theta, Matrix x) {
        // hypothesis is g(theta' * x) where g is the sigmoid function
        
        // (theta' * x')' is the same as x * theta
        return Sigmoid.RealSigmoid.evaluate(x.multiply(theta));
    }

}
