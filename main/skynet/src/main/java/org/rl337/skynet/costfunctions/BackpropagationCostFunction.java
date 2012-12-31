package org.rl337.skynet.costfunctions;

import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.types.Matrix;

public class BackpropagationCostFunction extends AbstractRegularizedCostFunction {

    public BackpropagationCostFunction(double lambda) {
        super(lambda);
    }

    @Override
    public Matrix gradient(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        return null;
    }

    @Override
    public Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y) {
        return null;
    }

}
