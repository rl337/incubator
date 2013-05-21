package org.rl337.skynet;

import org.rl337.skynet.hypothesis.LinearRegressionHypothesis;
import org.rl337.skynet.hypothesis.LogisticRegressionHypothesis;
import org.rl337.skynet.hypothesis.PerceptronHypothesis;
import org.rl337.math.types.Matrix;

public interface Hypothesis {
    Matrix guess(Matrix theta, Matrix x);
    
    public static final Hypothesis LinearRegression = new LinearRegressionHypothesis();
    public static final Hypothesis LogisticRegression = new LogisticRegressionHypothesis();
    public static final Hypothesis Perceptron = new PerceptronHypothesis();

}
