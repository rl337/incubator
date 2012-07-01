package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public class GradientDescentOptimizer implements Optimizer {

    public Matrix run(double alpha, int iterations, Matrix theta, Hypothesis h, CostFunction c, Matrix x, Matrix y) {
        
        int m = x.getRows();
        Matrix result = theta;

        for(int i = 0; i < iterations; i++) {
            // Calculates Gradient
            Matrix gradient = c.gradient(h, result, x, y);
            
           // Performs iteration
            result = result.subtract(gradient.multiply(alpha / m));
        }
        
        return result;
    }
    
}
