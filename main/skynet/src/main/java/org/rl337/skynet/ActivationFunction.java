package org.rl337.skynet;

import org.rl337.math.types.Matrix;

public interface ActivationFunction {
    Matrix evaluate(Matrix x);
}
