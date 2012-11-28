package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class ConjugateGradientOptimizer extends AbstractIterativeOptimizer {
    
    protected ConjugateGradientOptimizer(Hypothesis h, CostFunction c, int iterations, double epsilon, boolean debug) {
        super(h, c, iterations, epsilon, debug);
    }

    public Matrix run(Matrix theta, Matrix x, Matrix y) {
        
        Matrix result = theta;

        Matrix r = y.subtract(x.multiply(theta));
        Matrix p=r;
        double rsold=r.transpose().multiply(r).sum();
        
        int iterations = getIterations();
     
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

    @Override
    public Matrix runIteration(Matrix theta, Matrix gradient) {
        // TODO Auto-generated method stub
        return null;
    }

}
