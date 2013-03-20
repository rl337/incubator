package org.rl337.skynet.types;

import org.rl337.math.types.Matrix;


public class Log {
        private Matrix.MatrixOperation mLogFunction;
        
        private Log(Matrix.MatrixOperation function) {
            mLogFunction = function;
        }
        
        public Matrix evaluate(Matrix z) {
            return Matrix.matrixOperation(z, mLogFunction);
        }
        
        private static class LogFunction implements Matrix.MatrixOperation {
            public double operation(int row, int col, double x) {
                return Math.log(x);
            }
        }
        
        private static class LogPlusOneFunction implements Matrix.MatrixOperation {
            public double operation(int row, int col, double x) {
                double y = Math.log(x + 1);
                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    System.err.println("row: " + row + " produced a NaN (" + x + ")");
                }
                return y;
            }
        }
        
        public static final Log RealLog = new Log(new LogFunction());
        public static final Log RealLogPlusOne = new Log(new LogPlusOneFunction());

}
