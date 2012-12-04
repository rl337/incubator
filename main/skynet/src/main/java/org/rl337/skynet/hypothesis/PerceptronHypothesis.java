package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.BinaryThreshold;
import org.rl337.skynet.types.Matrix;

public class PerceptronHypothesis implements Hypothesis {

    public Matrix guess(Matrix theta, Matrix x) {
        return BinaryThreshold.Instance.evaluate(x.multiply(theta).sumColumns());
    }

}
