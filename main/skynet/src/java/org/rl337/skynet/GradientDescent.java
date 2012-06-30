package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public class GradientDescent {

    public Matrix run(double alpha, int iterations, Matrix theta, Hypothesis h, CostFunction c, Matrix x, Matrix y) {
        
        int m = x.getRows();
        Matrix result = theta;

        for(int i = 0; i < iterations; i++) {
            Matrix hx = h.guess(result, x);
            
            // Calculates Gradient
            Matrix deltas = hx.subtract(y);
            Matrix deltax = deltas.transpose().multiply(x).transpose();

           // Performs iteration
            result = result.subtract(deltax.multiply(alpha / m));
        }
        
        return result;
    }
    
    public static final GradientDescent instance = new GradientDescent();
}
