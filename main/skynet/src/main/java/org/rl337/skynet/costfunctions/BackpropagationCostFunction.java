package org.rl337.skynet.costfunctions;

import org.rl337.math.types.Matrix;
import org.rl337.skynet.Hypothesis;

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
