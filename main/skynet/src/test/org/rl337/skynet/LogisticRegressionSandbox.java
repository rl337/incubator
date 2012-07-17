package org.rl337.skynet;

import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;

public class LogisticRegressionSandbox {
    public static void main(String[] args) {
        Matrix testData = TestData.testLogisticRegressionMatrix(-2, -0.9, -10, 10, -10, 10, 1000);
        Matrix x = testData.sliceColumns(0, 1);
        final Matrix y = testData.sliceColumn(2);
        
        Sketchpad pad = new Sketchpad("Logistic Regression Sandbox", "debug", 640, 640);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.01, Hypothesis.LogisticRegression, CostFunction.LogisticRegression, true);
        Matrix optimalTheta = optimizer.run(
            Matrix.zeros(3,1), 
            x1,
            y,
            10000,
            1.0E-30
        );
        
        Matrix debugInfo = optimizer.getDebugData();
        if (debugInfo != null) {
            
            Matrix debugX = debugInfo.sliceColumn(0);
            Matrix debugY = Log.RealLog.evaluate(Matrix.ones(debugInfo.getRows(), 1).add(debugInfo.sliceColumn(1)));

            Matrix.Stats debugStats = debugY.stats();
            System.out.println("Debug Stats for Logistic Regression");
            System.out.println("   Size: " + debugInfo.getRows());
            System.out.println("    Min: " + debugStats.min);
            System.out.println("    Max: " + debugStats.max);
            System.out.println("   Mean: " + debugStats.mean);
            
            pad.plotScatterChart("debug", debugX, debugY);
        }
        
        Matrix cost = CostFunction.LogisticRegression.cost(Hypothesis.LogisticRegression, optimalTheta, x1, y);
        System.out.println("Completed learning: " + "\nTheta:\n" + optimalTheta + "Cost: " + cost);
        
        final Matrix hx = Hypothesis.LogisticRegression.guess(optimalTheta, x1);
        
        String drawing = "Label data";
        
        pad.plotScatterChart(drawing, x.sliceColumn(0), x.sliceColumn(1), new Sketchpad.ConditionalPlot() {
            public boolean valid(int row, int col, double xcoord, double ycoord) {
                return y.getValue(row, col) < 1;
            }
        });
        
        pad.plotScatterChart(drawing, x.sliceColumn(0), x.sliceColumn(1), new Sketchpad.ConditionalPlot() {
            public boolean valid(int row, int col, double xcoord, double ycoord) {
                return y.getValue(row, col) > 0;
            }
        });

        
        pad.plotScatterChart("Hypothesis", x.sliceColumn(0), x.sliceColumn(1));
        pad.plotScatterChart("Hypothesis", x.sliceColumn(0), x.sliceColumn(1), new Sketchpad.ConditionalPlot() {
            public boolean valid(int row, int col, double xcoord, double ycoord) {
                return hx.getValue(row, col) >= 0.5;
            }
        });
        
        int correct = 0;
        int total = y.getRows();
        for(int i = 0; i < total; i++) {
            double expected = y.getValue(i, 0);
            double actual = hx.getValue(i, 0);
            
            if ((actual < 0.5 && expected < 0.5) || (actual >= 0.5 && expected >= 0.5)) {
                correct++;
            }
        }
        
        System.out.println("Percent correct: " + ((double) correct * 100 / total));
    }
}
