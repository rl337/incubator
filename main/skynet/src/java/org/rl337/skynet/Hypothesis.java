package org.rl337.skynet;

import org.rl337.skynet.hypothesis.LinearRegressionHypothesis;
import org.rl337.skynet.types.Matrix;

public interface Hypothesis {
    Matrix guess(Matrix theta, Matrix x);
    
    public static final Hypothesis LinearRegression = new LinearRegressionHypothesis();
}
