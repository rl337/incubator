package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public class ConjugateGradientOptimizer extends Optimizer {
    
    protected ConjugateGradientOptimizer(Hypothesis h, CostFunction c) {
        super(h, c);
    }

    public Matrix run(Matrix theta, Matrix x, Matrix y, int iterations, double epsilon) {
        
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
