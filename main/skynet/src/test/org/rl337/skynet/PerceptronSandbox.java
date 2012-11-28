package org.rl337.skynet;

import java.io.File;

import org.rl337.skynet.optimizers.PerceptronOptimizer;
import org.rl337.skynet.types.Matrix;

public class PerceptronSandbox {
    public static void main(String[] args) throws Exception {
        
        Matrix optimalTheta = Matrix.zeros(785, 1); // 28 x 28 pixels + bias
        for(int i = 0; i < 1; i++) {
            PerceptronOptimizer optimizer = new PerceptronOptimizer();

            Matrix x = Matrix.loadMNISTPixelData(new File("data/train-images-idx3-ubyte.gz"), i, 5000);
            Matrix y = Matrix.loadMNISTLabelData(new File("data/train-labels-idx1-ubyte.gz"), i, 5000);
        
            Matrix x1 = Matrix.ones(x.getRows(), 1).appendColumns(x);
            optimalTheta = optimizer.run(
                optimalTheta, 
                x1,
                y
            );
        
        }
        
        System.out.println("Learning Complete.");
        
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
