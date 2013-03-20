package org.rl337.skynet.optimizers;

import org.rl337.skynet.GradientCostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

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
    public Matrix runIteration(Matrix theta, Matrix x, Matrix y) {
        
        Hypothesis h = getHypothesis();
        GradientCostFunction c = (GradientCostFunction) getCostFunction(); 
        
        // Calculates Gradient
        Matrix gradient = c.gradient(h, theta, x, y);
        
        return theta.subtract(gradient.multiply(mAlpha));
    }
    
}
