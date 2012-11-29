package org.rl337.skynet.datasets;

import java.io.File;

import org.rl337.skynet.types.Matrix;

import junit.framework.TestCase;

public class MNISTPixelDataSetTest extends TestCase {
    public void testTraining60kSetPixels() throws Exception {
        MNISTPixelDataSet ds = new MNISTPixelDataSet(new File("data/train-images-idx3-ubyte.gz"));

        Matrix m;
        int read = 0;
        while( (m = ds.getNextBatch(1)) != null) {
            assertEquals("MNIST pixel data should be 784 columns", 784, m.getColumns());
            assertEquals("This batch should be 1 row", 1, m.getRows());
            for(int i = 0; i < 784; i++) {
                assertTrue("pixel data must be less than 256", m.getValue(0, i) < 256);
                assertTrue("pixel data must be >= 0", m.getValue(0, i) >= 0);
            }
            read++;
        }
        
        assertEquals("MNIST training set should have 60k items", 60000, read);
    }
    
    public void testValidation10kSetPixels() throws Exception {
        MNISTPixelDataSet ds = new MNISTPixelDataSet(new File("data/t10k-images-idx3-ubyte.gz"));

        // This ds should have 60k entries and the values should all be between 0 and 9.
        Matrix m;
        int read = 0;
        while( (m = ds.getNextBatch(1000)) != null) {
            assertEquals("MNIST pixel data should be 784 columns", 784, m.getColumns());
            assertEquals("This batch should be 1000 rows", 1000, m.getRows());
            for(int i = 0; i < 1000; i++) {
                for(int j = 0; j < 784; j++) {
                    assertTrue("pixel data must be less than 256", m.getValue(i, j) < 256);
                    assertTrue("pixel data must be >= 0", m.getValue(i, j) >= 0);
                }
            }
            read++;
        }
        
        assertEquals("MNIST training set should have 10 batches of 1000", 10, read);
    }
}
