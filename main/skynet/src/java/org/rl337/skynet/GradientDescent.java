package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public class GradientDescent {

    public Matrix run(double alpha, int iterations, Matrix theta, Hypothesis h, CostFunction c, Matrix trials, Matrix observations) {
        
        int m = trials.getRows();
        Matrix result = theta;

        for(int i = 0; i < iterations; i++) {
            Matrix hx = h.guess(theta, trials);

            Matrix deltas = hx.subtract(observations);
            Matrix ones = Matrix.matrix(hx.getRows(), 1, 1.0);
            Matrix deltas1 = ones.appendColumns(deltas);

            Matrix deltax = deltas1.transpose().multiply(trials);
            result = result.subtract(deltax.sumRows().multiply(alpha / m).transpose());
        }
        
        return result;
    }
    
    public static final GradientDescent instance = new GradientDescent();
}
