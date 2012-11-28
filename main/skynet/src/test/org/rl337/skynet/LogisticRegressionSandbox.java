package org.rl337.skynet;

import java.io.File;

import org.rl337.skynet.Sketchpad.Shape;
import org.rl337.skynet.costfunctions.LogisticRegressionCostFunction;
import org.rl337.skynet.optimizers.GradientDescentOptimizer;
import org.rl337.skynet.types.Log;
import org.rl337.skynet.types.Matrix;

public class LogisticRegressionSandbox {
    public static void main(String[] args) throws Exception {
        
        Sketchpad pad = new Sketchpad("Logistic Regression Sandbox", "Cost over Iterations", 640, 640);

        Matrix optimalTheta = Matrix.zeros(785, 1); // 28 x 28 pixels + bias
        for(int i = 0; i < 1; i++) {
            CostFunction c = new LogisticRegressionCostFunction(0.1);
            GradientDescentOptimizer optimizer = new GradientDescentOptimizer(0.01, Hypothesis.LogisticRegression, c, 50000, 1.0E-30, true);

            Matrix x = Matrix.loadMNISTPixelData(new File("data/train-images-idx3-ubyte.gz"), i, 60000);
            Matrix y = Matrix.loadMNISTLabelData(new File("data/train-labels-idx1-ubyte.gz"), i, 60000);
        
            Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
            optimalTheta = optimizer.run(
                optimalTheta, 
                x1,
                y
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
                
                pad.plotScatterChart("Cost over Iterations", Shape.Circle, debugX, debugY);
            }
        }
        
        Matrix x = Matrix.loadMNISTPixelData(new File("data/t10k-images-idx3-ubyte.gz"), 0, 10000);
        Matrix y = Matrix.loadMNISTLabelData(new File("data/t10k-labels-idx1-ubyte.gz"), 0, 10000);
        
        Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
        final Matrix hx = Hypothesis.LogisticRegression.guess(optimalTheta, x1);
        
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
