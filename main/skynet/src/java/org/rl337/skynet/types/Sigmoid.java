package org.rl337.skynet.types;

public class Sigmoid {
    private Matrix.MatrixOperation mSigmoidFunction;
    
    private Sigmoid(Matrix.MatrixOperation function) {
        mSigmoidFunction = function;
    }
    
    public Matrix evaluate(Matrix z) {
        return Matrix.matrixOperation(z, mSigmoidFunction);
    }
    
    private static class SigmoidFunction implements Matrix.MatrixOperation {
        public double operation(double x) {
            return 1 / (1 + Math.pow(Math.E, -x));
        }
    }
    
    public static final Sigmoid RealSigmoid = new Sigmoid(new SigmoidFunction());

}
