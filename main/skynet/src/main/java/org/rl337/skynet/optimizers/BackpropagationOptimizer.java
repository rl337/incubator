package org.rl337.skynet.optimizers;

import org.rl337.skynet.GradientCostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class BackpropagationOptimizer extends AbstractIterativeOptimizer {

    protected BackpropagationOptimizer(Hypothesis h, GradientCostFunction c, int iterations, double epsilon, boolean debug) {
        super(h, c, iterations, epsilon, debug);
    }

    @Override
    public Matrix runIteration(Matrix theta, Matrix gradient) {
        return null;
    }

}
