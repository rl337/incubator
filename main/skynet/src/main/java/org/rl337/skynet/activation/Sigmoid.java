package org.rl337.skynet.activation;

import org.rl337.skynet.ActivationFunction;
import org.rl337.math.types.Matrix;

public class Sigmoid implements ActivationFunction {
    private Matrix.MatrixOperation mSigmoidFunction;
    
    private Sigmoid(Matrix.MatrixOperation function) {
        mSigmoidFunction = function;
    }
    
    public Matrix evaluate(Matrix z) {
        return Matrix.matrixOperation(z, mSigmoidFunction);
    }
    
    private static class SigmoidFunction implements Matrix.MatrixOperation {
        public double operation(int row, int col, double x) {
            return 1 / (1 + Math.pow(Math.E, -x));
        }
    }
    
    public static final Sigmoid RealSigmoid = new Sigmoid(new SigmoidFunction());

}
