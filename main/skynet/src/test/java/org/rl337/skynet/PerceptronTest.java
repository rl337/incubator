package org.rl337.skynet;

import java.io.File;

import org.rl337.skynet.datasets.MNISTLabelDataSet;
import org.rl337.skynet.datasets.MNISTPixelDataSet;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.PerceptronOptimizer;
import org.rl337.skynet.types.Matrix;
import org.rl337.skynet.types.Matrix.MatrixOperation;
import org.rl337.skynet.types.Sigmoid;

import junit.framework.TestCase;

public class PerceptronTest extends TestCase {

    public void testPerceptron() throws Exception {

        DataSet training = new MNISTPixelDataSet(new File("data/train-images-idx3-ubyte.gz"));
        DataSet labels = new MNISTLabelDataSet(new File("data/train-labels-idx1-ubyte.gz"));

        Matrix trainData = training.getNextBatch(1000);
        Matrix trainLabels = labels.getNextBatch(1000);
        
        final double fi = 3;
        Matrix realLabels = Matrix.matrixOperation(trainLabels, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return val == fi ? 1.0 : 0.0;
            }
        });
        
        PerceptronOptimizer optimizer = new PerceptronOptimizer(1000);
        Matrix theta = optimizer.run(Matrix.zeros(784, 1), new MatrixDataSet(trainData), new MatrixDataSet(realLabels));
        
        // add tests here
        
//        Sketchpad pad = new Sketchpad("Perceptron", "Perceptron Weights", 1024, 1024);
//        pad.plotAsBitmaps("Bitmaps", 28, 28, 5, 5, Sigmoid.RealSigmoid.evaluate(theta.transpose()));
//        Thread.sleep(50000);
    }
}
