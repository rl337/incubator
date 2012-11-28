package org.rl337.skynet.optimizers;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public class PerceptronOptimizer extends Optimizer {

    public PerceptronOptimizer() {
        super(Hypothesis.Perceptron, null);
    }

    @Override
    public Matrix run(Matrix theta, Matrix x, Matrix y) {
        
        Matrix guess = getHypothesis().guess(theta, x);
        for (int j = 0; j < guess.getRows(); j++) {
            double actualRaw = guess.getValue(j, 0);
            double actual = actualRaw >= 0.5 ? 1.0 : 0.0;
            double expected = y.getValue(j, 0);
            
            if (expected == actual) {
                continue;
            }
            
            Matrix m = x.sliceRow(j).transpose();
            if (expected == 0.0) {
                theta = theta.subtract(m);
            } else {
                theta = theta.add(m);
            }
        }
        
        return null;
    }

}
