package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public interface ActivationFunction {
    Matrix evaluate(Matrix x);
}
