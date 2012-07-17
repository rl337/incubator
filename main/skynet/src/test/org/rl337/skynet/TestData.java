package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixOperation;

public class TestData {
    public static Matrix testMatrix(final double intercept, final double slope, final double variance, int count) {
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
}
