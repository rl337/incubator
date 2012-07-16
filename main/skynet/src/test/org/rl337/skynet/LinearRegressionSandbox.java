package org.rl337.skynet;

import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Matrix;

public class LinearRegressionSandbox {

    public static void main(String[] args) {
        Matrix testData = TestData.testMatrix(4.0, 0.75, 0.1, 1000);
        Matrix x = testData.sliceColumn(0);
        Matrix y = testData.sliceColumn(1);
        
        String scatterPlotName = "Scatter X vs Y";
        Sketchpad pad = new Sketchpad("test plot", scatterPlotName, 640, 640);
        pad.plotScatterChart(scatterPlotName, x, y);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.0125, Hypothesis.LinearRegression, CostFunction.DifferenceSquare);
        Matrix theta = optimizer.run(
            Matrix.zeros(2,1), 
            x1,
            y,
            10000
        );
        
        System.out.println("Completed learning: " + theta);
        Matrix hx = Hypothesis.LinearRegression.guess(theta, x1);
        pad.plotScatterChart(scatterPlotName, x, hx);
    }
}
