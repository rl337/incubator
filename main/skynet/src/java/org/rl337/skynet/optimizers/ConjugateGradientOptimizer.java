package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public class ConjugateGradientOptimizer implements Optimizer {
    
    public Matrix run(double alpha2, int iterations, Matrix theta, Hypothesis h, CostFunction c, Matrix x, Matrix y) {
        
        Matrix result = theta;

        Matrix r = y.subtract(x.multiply(theta));
        Matrix p=r;
        double rsold=r.transpose().multiply(r).sum();
     
        for (int i = 0; i < iterations; i++) {
            Matrix Ap = x.multiply(p);
            double alpha = rsold / (p.transpose().multiply(Ap)).sum();
            
            theta = theta.add(p.multiply(alpha));
            r = r.subtract(Ap.multiply(alpha));
            double rsnew= r.transpose().multiply(r).sum();
            if (Math.sqrt(rsnew) < 1.0e-10) {
                  break;
            }
            
            p = r.add(p.multiply(rsnew / rsold));
            rsold=rsnew;
        }
        
        return result;
    }

}
