package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.activation.BinaryThreshold;
import org.rl337.math.types.Matrix;

public class PerceptronHypothesis implements Hypothesis {

    public Matrix guess(Matrix theta, Matrix x) {
        return BinaryThreshold.Instance.evaluate(x.multiply(theta));
    }

}
