package org.rl337.skynet.types;

import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixOperation;

public class Normalize {
    public Matrix evaluate(Matrix m) {
        final Matrix.Stats stats = m.stats();
        
        return Matrix.matrixOperation(m, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return (val - stats.mean) / stats.deviation;
            }
            
        });
    }
    
    public static final Normalize normalize = new Normalize();
}
