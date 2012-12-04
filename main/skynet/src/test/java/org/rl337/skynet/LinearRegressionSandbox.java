package org.rl337.skynet;

import org.rl337.skynet.Sketchpad.Shape;
import org.rl337.skynet.costfunctions.DifferenceSquareCostFunction;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;

public class LinearRegressionSandbox {

    
    public static void main(String[] args) {
        //Matrix testData = TestData.testMatrixEx(2, 0, 2.5, 1000);
        Matrix testData = TestData.testMatrixSine(2.25, 0.5, 3, 0, 0.1, 2000);
        //Matrix testData = TestData.testMatrixLinear(0.5, 0.4, 0.1, 1000);
        
        Matrix x = testData.sliceColumn(0);
        Matrix y = testData.sliceColumn(1);
        
        Matrix f = TestData.generatePolynominalFeatures(x, 15);
        //Matrix fnorm = Normalize.normalize.evaluate(f);
        Matrix fnorm = f;
        
        String scatterPlotName = "Scatter X vs Y";
        Sketchpad pad = new Sketchpad("test plot", scatterPlotName, 640, 640);
        pad.plotScatterChart(scatterPlotName, Shape.Circle, x, y);
        
        CostFunction c = new DifferenceSquareCostFunction(1.0);
        
        //Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.0025, Hypothesis.LinearRegression, c, 40000, 0, true);
        Matrix theta = optimizer.run(
            Matrix.zeros(fnorm.getColumns(),1), 
            new MatrixDataSet(fnorm),
            new MatrixDataSet(y)
        );
        
        Matrix debugInfo = optimizer.getDebugData();
        if (debugInfo != null) {
            
            Matrix debugX = debugInfo.sliceColumn(0);
            Matrix debugY = Log.RealLog.evaluate(Matrix.ones(debugInfo.getRows(), 1).add(debugInfo.sliceColumn(1)));

            Matrix.Stats debugStats = debugY.stats();
            System.out.println("Debug Stats for " + scatterPlotName);
            System.out.println("   Size: " + debugInfo.getRows());
            System.out.println("    Min: " + debugStats.min);
            System.out.println("    Max: " + debugStats.max);
            System.out.println("   Mean: " + debugStats.mean);
            
            pad.plotScatterChart("debug", Shape.X, debugX, debugY);
            
        }
        
        Matrix cost = c.cost(Hypothesis.LinearRegression, theta, fnorm, y);
        System.out.println("Completed learning for: " + scatterPlotName + "\nTheta:\n" + theta + "Cost: " + cost);
        
        Matrix hx = Hypothesis.LinearRegression.guess(theta, fnorm);
        pad.plotScatterChart(scatterPlotName, Shape.Square, x, hx);
        
        
    }
}
