package org.rl337.skynet;

import junit.framework.TestCase;

import org.rl337.skynet.costfunctions.LogisticRegressionCostFunction;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Matrix;

public class LogisticRegressionGradientDescentTest extends TestCase {
    
    public Matrix runLogisticRegression(Matrix x, Matrix y, int iterations, double alpha) {
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientCostFunction c = new LogisticRegressionCostFunction(1.0);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(alpha, Hypothesis.LogisticRegression, c, iterations, 0);
        Matrix theta = optimizer.run(
            Matrix.zeros(3,1), 
            new MatrixDataSet(x1),
            new MatrixDataSet(y)
        );
        
        return Hypothesis.LogisticRegression.guess(theta, x1);
    }
    
    public void testLogisticRegression45DegreesNoIntercept() {
        Matrix testData = TestData.testLogisticRegressionMatrix(0.0, 0.0, -10, 10, -10, 10, 1000);
        
        Matrix x = testData.sliceColumns(0, 1);
        Matrix y = testData.sliceColumn(2);
        
        Matrix h = runLogisticRegression(x, y, 10000, 0.01);
        
        int wrong = 0;
        for (int i = 0; i < y.getRows(); i++) {
            int actual = h.getValue(i, 0) >= 0.5 ? 1 : 0;
            int expected = y.getValue(i, 0) >= 0.5 ? 1 : 0;
            
            if (actual != expected) {
                wrong++;
            }
        }
        
        double percentWrong = wrong * 100.0 / y.getRows();
        assertTrue("We expect at least 99% right", percentWrong < 1.0);
    }
    
    public void testLogisticRegression60Degrees4Intercept() {
        Matrix testData = TestData.testLogisticRegressionMatrix(4.0, 3.0 / 2.0, 0, 1000, 0, 1000, 1000);

        Matrix x = testData.sliceColumns(0, 1);
        Matrix y = testData.sliceColumn(2);
        
        Matrix h = runLogisticRegression(x, y, 15000, 0.0003125);
        
        int wrong = 0;
        for (int i = 0; i < y.getRows(); i++) {
            int actual = h.getValue(i, 0) >= 0.5 ? 1 : 0;
            int expected = y.getValue(i, 0) >= 0.5 ? 1 : 0;
            
            if (actual != expected) {
                wrong++;
            }
        }
        
        double percentWrong = wrong * 100.0 / y.getRows();
        assertTrue("We expect at least 99% right. Was " + (100.0 - percentWrong), percentWrong < 1.0);
    }

}
