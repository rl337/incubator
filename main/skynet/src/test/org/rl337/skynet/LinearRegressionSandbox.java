package org.rl337.skynet;

import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Normalize;

public class LinearRegressionSandbox {

    public static void main(String[] args) {
        Matrix testData = TestData.testMatrix(4.0, 0.75, 0.1, 1000);
        Matrix x = testData.sliceColumn(0);
        Matrix y = testData.sliceColumn(1);
        Matrix ynorm = Normalize.normalize.evaluate(y);
        
        String scatterPlotName = "Scatter X vs Y";
        Sketchpad pad = new Sketchpad("test plot", scatterPlotName, 640, 640);
        pad.plotScatterChart(scatterPlotName, x, ynorm);
        
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.01, Hypothesis.LinearRegression, CostFunction.DifferenceSquare, true);
        Matrix theta = optimizer.run(
            Matrix.zeros(2,1), 
            x1,
            ynorm,
            100000,
            1.0E-30
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
            
            pad.plotScatterChart("debug", debugX, debugY);
            
        }
        
        Matrix cost = CostFunction.DifferenceSquare.cost(Hypothesis.LinearRegression, theta, x1, ynorm);
        System.out.println("Completed learning for: " + scatterPlotName + "\nTheta:\n" + theta + "Cost: " + cost);
        
        Matrix hx = Hypothesis.LinearRegression.guess(theta, x1);
        pad.plotScatterChart(scatterPlotName, x, hx);
    }
}
