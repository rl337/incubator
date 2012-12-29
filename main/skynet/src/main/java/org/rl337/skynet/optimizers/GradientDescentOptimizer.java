package org.rl337.skynet.optimizers;

import org.rl337.skynet.GradientCostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class GradientDescentOptimizer extends AbstractIterativeOptimizer {
    private double mAlpha;

    public GradientDescentOptimizer(double alpha, Hypothesis h, GradientCostFunction c, int maxIterations, double epsilon, boolean debug) {
        super(h, c, maxIterations, epsilon, debug);
        mAlpha = alpha;
    }

    
    public GradientDescentOptimizer(double alpha, Hypothesis h, GradientCostFunction c, int maxIterations, double epsilon) {
        this(alpha, h, c, maxIterations, epsilon, false);
    }

    @Override
    public Matrix runIteration(Matrix theta, Matrix gradient) {
        return theta.subtract(gradient.multiply(mAlpha));
    }
    
}
