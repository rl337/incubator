package org.rl337.skynet.hypothesis;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class PerceptronHypothesis implements Hypothesis {

    @Override
    public Matrix guess(Matrix theta, Matrix x) {
        return x.multiplyElementWise(theta);
    }

}
