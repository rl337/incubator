package org.rl337.skynet;

import java.util.Random;

import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixOperation;
import org.rl337.skynet.types.Normalize;

public class LinearRegressionSandbox {
    
    public static Sketchpad learnAndPlotSet(String name, Sketchpad pad, Matrix x, Matrix y) {
        if (pad == null) {
            Matrix.Stats xStats = x.stats();
            Matrix.Stats yStats = y.stats();
            pad = new Sketchpad("Linear Regression Sandbox", name, 640, 640, 0.0, xStats.max, 0.0, yStats.max);
        }
        
        pad.plotScatterChart(name, x, y);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.0003, Hypothesis.LinearRegression, CostFunction.DifferenceSquare, true);
        Matrix theta = optimizer.run(
            Matrix.zeros(2,1), 
            x1,
            y,
            100000,
            1.0E-30
        );
        
        Matrix debugInfo = optimizer.getDebugData();
        if (debugInfo != null) {
            
            Matrix debugX = debugInfo.sliceColumn(0);
            Matrix debugY = Log.RealLog.evaluate(Matrix.ones(debugInfo.getRows(), 1).add(debugInfo.sliceColumn(1)));

            Matrix.Stats debugStats = debugY.stats();
            System.out.println("Debug Stats for " + name);
            System.out.println("   Size: " + debugInfo.getRows());
            System.out.println("    Min: " + debugStats.min);
            System.out.println("    Max: " + debugStats.max);
            System.out.println("   Mean: " + debugStats.mean);
            
            pad.plotScatterChart("debug", debugX, debugY);
            
        }
        
        Matrix cost = CostFunction.DifferenceSquare.cost(Hypothesis.LinearRegression, theta, x1, y);
        System.out.println("Completed learning for: " + name + "\nTheta:\n" + theta + "Cost: " + cost);
        Matrix hx = Hypothesis.LinearRegression.guess(theta, x1);
        pad.plotScatterChart(name, x, hx);
        
        return pad;
    }

    public static void main(String[] args) {
       
        Matrix x = Matrix.matrixOperation(Matrix.zeros(1000, 1), new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return row * 0.001;
            }
        });
        
        final Random rand = new Random(1024L);
        Matrix y = Matrix.matrixOperation(Matrix.zeros(1000, 1), new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return row * 0.5 + rand.nextDouble() * 50;
            }
        });
        
        
        Sketchpad pad = learnAndPlotSet("Non-normalized", null, x, y);
        
        Matrix xNorm = Normalize.normalize.evaluate(x);
        Matrix yNorm = Normalize.normalize.evaluate(y);
        
        learnAndPlotSet("Normalized", pad, xNorm, yNorm);

    }
}
