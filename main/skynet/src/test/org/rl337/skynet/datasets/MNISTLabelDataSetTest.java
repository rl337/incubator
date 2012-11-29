package org.rl337.skynet.datasets;

import java.io.File;

import org.rl337.skynet.types.Matrix;

import junit.framework.TestCase;

public class MNISTLabelDataSetTest extends TestCase {
    public void testTraining60kSetLabels() throws Exception {
        MNISTLabelDataSet ds = new MNISTLabelDataSet(new File("data/train-labels-idx1-ubyte.gz"));

        // This ds should have 60k entries and the values should all be between 0 and 9.
        Matrix m;
        int read = 0;
        while( (m = ds.getNextBatch(1)) != null) {
            assertEquals("MNIST Label data should be 1 column", 1, m.getColumns());
            assertEquals("This batch should be 1 row", 1, m.getRows());
            assertTrue("label data must be less than 10", m.getValue(0, 0) < 10);
            assertTrue("label data must be >= 0", m.getValue(0, 0) >= 0);
            read++;
        }
        
        assertEquals("MNIST training set should have 60k items", 60000, read);
    }
    
    public void testValidation10kSetLabels() throws Exception {
        MNISTLabelDataSet ds = new MNISTLabelDataSet(new File("data/t10k-labels-idx1-ubyte.gz"));

        // This ds should have 60k entries and the values should all be between 0 and 9.
        Matrix m;
        int read = 0;
        while( (m = ds.getNextBatch(1000)) != null) {
            assertEquals("MNIST Label data should be 1 column", 1, m.getColumns());
            assertEquals("This batch should be 1000 rows", 1000, m.getRows());
            for(int i = 0; i < 1000; i++) {
                assertTrue("label data must be less than 10", m.getValue(i, 0) < 10);
                assertTrue("label data must be >= 0", m.getValue(i, 0) >= 0);
            }
            read++;
        }
        
        assertEquals("MNIST training set should have 10 batches of 1000", 10, read);
    }
}
