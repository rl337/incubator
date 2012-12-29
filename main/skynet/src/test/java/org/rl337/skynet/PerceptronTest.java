package org.rl337.skynet;

import java.io.File;

import junit.framework.TestCase;

import org.rl337.skynet.datasets.MNISTLabelDataSet;
import org.rl337.skynet.datasets.MNISTPixelDataSet;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.PerceptronOptimizer;
import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixElementWiseOperation;
import org.rl337.skynet.types.Matrix.MatrixOperation;

public class PerceptronTest extends TestCase {

    public void testPerceptron() throws Exception {

        DataSet training = new MNISTPixelDataSet(new File("data/train-images-idx3-ubyte.gz"), true);
        DataSet labels = new MNISTLabelDataSet(new File("data/train-labels-idx1-ubyte.gz"));

        Matrix theta = Matrix.zeros(785, 1);
        Optimizer optimizer = new PerceptronOptimizer(100);
        for(int i = 0; i < 60; i++) {
            Matrix trainData = training.getNextBatch(1000).divide(255);
            Matrix trainLabels = labels.getNextBatch(1000);
            
             Matrix realLabels = Matrix.matrixOperation(trainLabels, new MatrixOperation() {
                public double operation(int row, int col, double val) {
                  return ((int) val) % 2 == 1 ? 0.0 : 1.0;
                }
            });

            theta = optimizer.run(theta, new MatrixDataSet(trainData), new MatrixDataSet(realLabels));
        }
        
        // add tests here
        DataSet test = new MNISTPixelDataSet(new File("data/t10k-images-idx3-ubyte.gz"), true);
        DataSet testlabels = new MNISTLabelDataSet(new File("data/t10k-labels-idx1-ubyte.gz"));

        Hypothesis h = optimizer.getHypothesis();
        Matrix validationData = test.getNextBatch(5000).divide(255);
        Matrix validationLabels = testlabels.getNextBatch(5000);
        Matrix realValidationLabels = Matrix.matrixOperation(validationLabels, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return ((int) val) % 2 == 1 ? 0.0 : 1.0;
            }
        });
        
        Matrix guess = h.guess(theta, validationData);
        Matrix correct = Matrix.elementWiseOperation("Compare", realValidationLabels, guess, new MatrixElementWiseOperation() {

            public double operation(int row, int col, double aVal, double bVal) {
                return aVal == bVal ? 1.0 : 0.0;
            }
            
        });
        
        double percentCorrect = (double) correct.sum() / correct.getRows() * 100;
        
        //System.out.println(percentCorrect);
        assertTrue("We're expecting a success rate of at least 80%. Actual: " + percentCorrect, percentCorrect > 80.0);
        
//        Sketchpad pad = new Sketchpad("Perceptron", "Perceptron Weights", 1024, 1024);
//        pad.plotAsBitmaps("Perceptron Weights", 28, 28, 5, 5, Sigmoid.RealSigmoid.evaluate(theta.sliceRows(1,784).transpose()));
//        Thread.sleep(50000);
    }
}
