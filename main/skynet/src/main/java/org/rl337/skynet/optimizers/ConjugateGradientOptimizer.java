package org.rl337.skynet.optimizers;

import org.rl337.skynet.GradientCostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

public class ConjugateGradientOptimizer extends AbstractIterativeOptimizer {
    
    protected ConjugateGradientOptimizer(Hypothesis h, GradientCostFunction c, int iterations, double epsilon, boolean debug) {
        super(h, c, iterations, epsilon, debug);
    }

    @Override
    public Matrix runIteration(Matrix theta, Matrix x, Matrix y) {
        Matrix r = y.subtract(x.multiply(theta));

        double rsold = r.transpose().multiply(r).sum();
        Matrix Ap = x.multiply(r);
        double alpha = rsold / (r.transpose().multiply(Ap)).sum();
        
        theta = theta.add(r.multiply(alpha));

        
        return theta;
    }

}
