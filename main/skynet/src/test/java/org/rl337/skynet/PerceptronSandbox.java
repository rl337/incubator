package org.rl337.skynet;

import java.io.File;

import org.rl337.skynet.activation.Sigmoid;
import org.rl337.skynet.datasets.MNISTLabelDataSet;
import org.rl337.skynet.datasets.MNISTPixelDataSet;
import org.rl337.skynet.datasets.MatrixDataSet;
import org.rl337.skynet.optimizers.PerceptronOptimizer;
import org.rl337.math.Sketchpad;
import org.rl337.math.types.Matrix;
import org.rl337.math.types.Matrix.MatrixOperation;

public class PerceptronSandbox {
    public static void main(String[] args) throws Exception {
        // We have 10 different labels, so we need 10 thetas and 10 optimizers
        Optimizer[] optimizers = new Optimizer[10];
        Matrix[] thetas = new Matrix[10];
        
        Sketchpad pad = new Sketchpad("Perceptron", "Perceptron Weights", 1024, 1024);

        for(int i = 0; i < 10; i++) {
            optimizers[i] = new PerceptronOptimizer(1000, 0.1);
            thetas[i] = Matrix.zeros(784, 1);
        }
        
        DataSet training = new MNISTPixelDataSet(new File("data/train-images-idx3-ubyte.gz"));
        DataSet labels = new MNISTLabelDataSet(new File("data/train-labels-idx1-ubyte.gz"));
        int batch = 1;
        while(labels.hasMore() && training.hasMore()) {
            Matrix batchLabels = labels.getNextBatch(10000);
            Matrix batchPixels = training.getNextBatch(10000);
            
            if (batchLabels == null || batchPixels == null) {
                break;
            }
            
            batchPixels = batchPixels.divide(255.0);

            for(int i = 0; i < 10; i++) {
                final double fi = i;
                Matrix realLabels = Matrix.matrixOperation(batchLabels, new MatrixOperation() {
                    public double operation(int row, int col, double val) {
                        return val == fi ? 1.0 : 0.0;
                    }
                });
                
                thetas[i] = optimizers[i].run(
                    thetas[i], 
                    new MatrixDataSet(batchPixels),
                    new MatrixDataSet(realLabels)
                );
            }
            System.out.println("Completed batch " + batch);
            
            Matrix allThetas = thetas[0];
            for(int i = 1; i < thetas.length; i++) {
                allThetas = allThetas.appendColumns(thetas[i]);
            }
            
            pad.plotAsBitmaps("Perceptron Weights Iteration " + batch, 28, 28, 1, 10, Sigmoid.RealSigmoid.evaluate(allThetas.transpose()));
            batch++;
        }

        System.out.println("Learning Complete.");
        
        DataSet test = new MNISTPixelDataSet(new File("data/t10k-images-idx3-ubyte.gz"));
        DataSet testlabels = new MNISTLabelDataSet(new File("data/t10k-labels-idx1-ubyte.gz"));
        
        Matrix y = testlabels.getNextBatch(1000);
        Matrix x = test.getNextBatch(1000).divide(255.0);
        
        Matrix hx = Matrix.zeros(y.getRows(), y.getColumns());
        for(int i = 0; i < 10; i++) {
            Hypothesis h = optimizers[i].getHypothesis();
            Matrix hi = h.guess(thetas[i], x).multiply(i);
            hx.add(hi);
        }
        
        int correct = 0;
        int total = y.getRows();
        for(int i = 0; i < total; i++) {
            double expected = y.getValue(i, 0);
            double actual = hx.getValue(i, 0);
            System.out.println("expected: " + expected + " actual: " + actual);
            if (expected == actual) {
                correct++;
            }
        }
        
        System.out.println("Percent correct: " + ((double) correct * 100 / total));
        
        Thread.sleep(50000);
    }
}
