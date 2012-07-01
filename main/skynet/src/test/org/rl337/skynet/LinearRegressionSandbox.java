package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public class LinearRegressionSandbox {

    public static void main(String[] args) {
        Matrix x = Matrix.random(1000, 1, 0.0, 1.0);
        Matrix y = Matrix.random(1000, 1, 0.75, 1.0);
        
        String scatterPlotName = "Scatter X vs Y";
        Sketchpad pad = new Sketchpad("test plot", scatterPlotName, 640, 640, 0.0, 1.0, 0.0, 1.0);
        pad.plotScatterChart(scatterPlotName, x, y);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        Matrix theta = Optimizer.GradientDescent.run(
            0.0125,
            10000,
            Matrix.zeros(2,1), 
            Hypothesis.LinearRegression,
            CostFunction.DifferenceSquare,
            x1,
            y
        );
        
        System.out.println("Completed learning: " + theta);
        Matrix hx = Hypothesis.LinearRegression.guess(theta, x1);
        pad.plotScatterChart(scatterPlotName, x, hx);
    }
}
