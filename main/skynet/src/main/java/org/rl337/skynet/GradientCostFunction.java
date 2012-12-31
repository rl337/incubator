package org.rl337.skynet;

import org.rl337.math.types.Matrix;

public interface GradientCostFunction extends CostFunction {
    Matrix gradient(Hypothesis h, Matrix theta, Matrix x, Matrix y);
}
