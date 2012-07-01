package org.rl337.skynet.types;


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
        
        public static final Log RealLog = new Log(new LogFunction());

}
