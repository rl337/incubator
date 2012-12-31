package org.rl337.skynet.hypothesis;

import org.rl337.skynet.ActivationFunction;
import org.rl337.skynet.Hypothesis;
import org.rl337.math.types.Matrix;

public class FeedForwardNeuralNetworkHypothesis implements Hypothesis {
    ActivationFunction mActivation;
    
    public FeedForwardNeuralNetworkHypothesis(ActivationFunction a) {
        mActivation = a;
    }

    @Override
    public Matrix guess(Matrix theta, Matrix x) {
        
        Matrix a = null;
        for(int i = 0; i < x.getRows(); i++) {
            a = x.sliceRow(i).transpose();
            for(int j = 0; j < theta.getRows(); j++) {
                Matrix t = theta.sliceRow(i);
                Matrix z = t.multiply(a);
                a = mActivation.evaluate(z);
            }
        }
        
        return a;
    }

    
}
