package org.rl337.skynet;

import org.rl337.skynet.optimizers.ConjugateGradientOptimizer;
import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Matrix;

public interface Optimizer {
    Matrix run(double alpha, int iterations, Matrix theta, Hypothesis h, CostFunction c, Matrix x, Matrix y);
    
    public static final Optimizer GradientDescent = new GradientDescentOptimizer();
    public static final Optimizer ConjugateGradient = new ConjugateGradientOptimizer();
}
