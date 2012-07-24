package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixOperation;

public class TestData {
    public static Matrix testMatrixLinear(final double intercept, final double slope, final double variance, int count) {
        double min = -variance / 2;
        double max = variance / 2;
        
        final Matrix domain = Matrix.random(count, 1, 0.0, 1.0);
        Matrix noise = Matrix.random(count, 1, min, max);
        Matrix range = Matrix.matrixOperation(noise, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return domain.getValue(row, col) * slope + val + intercept;
            }
        });
        
        return domain.appendColumns(range);
    }
    
    public static Matrix testMatrixSine(final double period, final double amplitude, final double phase, final double center, final double variance, int count) {
        double min = -variance / 2;
        double max = variance / 2;
        
        final Matrix domain = Matrix.random(count, 1, -1.0, 1.0);
        Matrix noise = Matrix.random(count, 1, min, max);
        Matrix range = Matrix.matrixOperation(noise, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return Math.sin(2 * Math.PI / period * (domain.getValue(row, col) + phase)) * amplitude + center + val;
            }
        });
        
        return domain.appendColumns(range);
    }
    
    public static Matrix testMatrixEx(final double coefficient, final double intercept, final double variance, int count) {
        double min = -variance / 2;
        double max = variance / 2;
        
        final Matrix domain = Matrix.random(count, 1, -1.0, 1.0);
        Matrix noise = Matrix.random(count, 1, min, max);
        Matrix range = Matrix.matrixOperation(noise, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return Math.pow(Math.E, domain.getValue(row, col) * coefficient) + intercept + val;
            }
        });
        
        return domain.appendColumns(range);
    }
    
    public static Matrix testLogisticRegressionMatrix(final double intercept, final double slope, final double minX, final double maxX, final double minY, final double maxY, int count) {
        final Matrix feature1 = Matrix.random(count, 1, minX, maxX);
        Matrix feature2 = Matrix.random(count, 1, minY, maxY);
        
        Matrix labels = Matrix.matrixOperation(feature2, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                double decisionBoundary = slope * feature1.getValue(row, col) + intercept;
                return val > decisionBoundary ? 1.0 : 0.0;
            }
        });
        
        Matrix features = feature1.appendColumns(feature2);
        
        return features.appendColumns(labels);
     }
    
    public static Matrix generatePolynominalFeatures(Matrix x, int degree) {

        Matrix features = Matrix.ones(x.getRows(), 1);
        for(int i = 1; i < degree; i++) {
            Matrix x1degree = x.pow(i);
            features = features.appendColumns(x1degree);
        }
        
        return features;
    }
}
