package org.rl337.skynet;

import java.io.File;

import junit.framework.TestCase;

import org.rl337.math.types.Matrix;
import org.rl337.math.types.Matrix.MatrixElementWiseOperation;
import org.rl337.math.types.Matrix.MatrixOperation;
import org.rl337.skynet.datasets.MNISTLabelDataSet;
import org.rl337.skynet.datasets.MNISTPixelDataSet;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.PerceptronOptimizer;

public class PerceptronTest extends TestCase {

    public void testPerceptron() throws Exception {

        DataSet training = new MNISTPixelDataSet(new File("data/train-images-idx3-ubyte.gz"), true);
        DataSet labels = new MNISTLabelDataSet(new File("data/train-labels-idx1-ubyte.gz"));

        Matrix theta = Matrix.zeros(785, 1);
        Optimizer optimizer = new PerceptronOptimizer(100, 0.1);
        
//        Sketchpad pad = new Sketchpad("Perceptron", "Perceptron Weights", 1024, 1024);
        final int number = 3;
        for(int i = 0; i < 60; i++) {
            Matrix trainData = training.getNextBatch(1000).divide(255);
            Matrix trainLabels = labels.getNextBatch(1000);
            
             Matrix realLabels = Matrix.matrixOperation(trainLabels, new MatrixOperation() {
                public double operation(int row, int col, double val) {
                  return ((int) val) == number ? 1.0 : 0.0;
                }
            });

            theta = optimizer.run(theta, new MatrixDataSet(trainData), new MatrixDataSet(realLabels));
//            if (i % 10 == 0) {
//                pad.setCursor(128 + (130 * i / 10), 128);
//                
//                pad.plotAsBitmaps("Perceptron Weights Iteration", 28, 28, 5, 5, Sigmoid.RealSigmoid.evaluate(theta.sliceRows(1,784).transpose()));
//            }
        }
        
        // add tests here
        DataSet test = new MNISTPixelDataSet(new File("data/t10k-images-idx3-ubyte.gz"), true);
        DataSet testlabels = new MNISTLabelDataSet(new File("data/t10k-labels-idx1-ubyte.gz"));

        Hypothesis h = optimizer.getHypothesis();
        Matrix validationData = test.getNextBatch(5000).divide(255);
        Matrix validationLabels = testlabels.getNextBatch(5000);
        Matrix realValidationLabels = Matrix.matrixOperation(validationLabels, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return ((int) val) == number ? 1.0 : 0.0;
            }
        });
        
        Matrix guess = h.guess(theta, validationData);
        Matrix correct = Matrix.elementWiseOperation("Compare", realValidationLabels, guess, new MatrixElementWiseOperation() {

            public double operation(int row, int col, double aVal, double bVal) {
                if (aVal == bVal) {
                    if (aVal == 0.0) {
                        // zero is correct Negative.
                        return 0.0;
                    }
                    
                    // one is correct Positive
                    return 1.0;
                }
                
                // false negative.
                if (aVal == 0) {
                    return 2.0; 
                }
                
                // false positive
                return 3.0;
            }
            
        });
        
        int[] dist = new int[4];
        for(int i = 0; i < correct.getRows(); i++) {
            int val = (int) correct.getValue(i, 0);
            
            dist[val]++;
        }
        
        double precision = ((double) dist[1] * 100) / (dist[1] + dist[3]);
        double recall = ((double) dist[1] * 100) / (dist[1] + dist[2]);
        
//        System.out.println(
//                "Correct Negative " + dist[0] + "\n" +
//                "Correct Positive " + dist[1] + "\n" +
//                "False Negative " + dist[2] + "\n" +
//                "False Positive " + dist[3] + "\n" + 
//                "precision " + precision + "\n" +
//                "recall " + recall + "\n"
//            );
        
        // Perceptrons really aren't that good at detecting MNIST.
        
        // We want to verify that the optimizer isn't simply guessing something like 0 all the time.
        // If that were the case, we should get about 0% precision and NaN recall.  What we'd like is
        // at least 90% precision (same as if we always guessed negative) and at least a 50% correct guess
        // on the cases that matter.
        
        
        assertTrue("Precision should be at least 90%: " + precision, precision > 90.0);
        assertTrue("We want our recall to be at least 40%: " + recall, recall > 40.0);
    }
}
