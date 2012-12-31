package org.rl337.skynet;

import org.rl337.math.types.Matrix;

public interface CostFunction {
    Matrix cost(Hypothesis h, Matrix theta, Matrix x, Matrix y);
}
