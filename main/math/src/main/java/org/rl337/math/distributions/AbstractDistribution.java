package org.rl337.math.distributions;

import org.rl337.math.Distribution;
import org.rl337.math.types.Matrix;
import org.rl337.math.types.Matrix.MatrixOperation;

public abstract class AbstractDistribution implements Distribution {

    public double pr(double a, double b) {
        return pr(b) - pr(a);
    }
    
    Matrix samples(int i) {
        Matrix m = Matrix.random(i, 1, Double.MIN_VALUE, Double.MAX_VALUE);
        return Matrix.matrixOperation(m, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return q(val);
            }
        });
    }

}
