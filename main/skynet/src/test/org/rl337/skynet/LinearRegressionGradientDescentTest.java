package org.rl337.skynet;

import org.rl337.skynet.costfunctions.DifferenceSquareCostFunction;
import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Matrix;
import junit.framework.TestCase;

public class LinearRegressionGradientDescentTest extends TestCase {
    
    public Matrix runLinearRegression(Matrix x, Matrix y, int iterations, double alpha) {
        
        CostFunction c = new DifferenceSquareCostFunction(0.1);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(alpha, Hypothesis.LinearRegression, c, iterations, 0.0);
        Matrix theta = optimizer.run(
            Matrix.zeros(2,1), 
            x1,
            y
        );
        
        return theta;
    }
    
    public void testLinearRegression45DegreesNoIntercept() {
        Matrix testData = TestData.testMatrixLinear(0.0, 1.0, 0.1, 1000);
        Matrix x = testData.sliceColumn(0);
        Matrix y = testData.sliceColumn(1);
        
        Matrix theta = runLinearRegression(x, y, 10000, 0.01);
        assertEquals("theta(0) should be expected", 0.0, theta.getValue(0, 0), 0.01);
        assertEquals("theta(1) should be expected", 1.0, theta.getValue(1, 0), 0.01);
    }
    
    public void testLinearRegression60Degrees4Intercept() {
        Matrix testData = TestData.testMatrixLinear(4.0, 3.0 / 2.0, 0.1, 1000);
        Matrix x = testData.sliceColumn(0);
        Matrix y = testData.sliceColumn(1);
        
        Matrix theta = runLinearRegression(x, y, 10000, 0.01);
        assertEquals("theta(0) should be expected", 4.0, theta.getValue(0, 0), 0.01);
        assertEquals("theta(1) should be expected", 3.0 / 2.0, theta.getValue(1, 0), 0.01);
    }

}
