package org.rl337.skynet.datasets;

import java.io.File;
import java.io.FileOutputStream;

import org.rl337.math.types.Matrix;

import junit.framework.TestCase;

public class DelimitedTextFileDataSetTest extends TestCase {
    public void testGetAll() throws Exception {
        
        double[][] mv = new double[][] {
            {9, 8, 7},
            {1, 3, 5},
            {23, 25, 29}
        };
        
        File f = createTestDataSet(mv, ",");
        DelimitedTextFileDataSet ds = new DelimitedTextFileDataSet(f, ",");
        
        Matrix a = ds.getAll();
        assertMatrix(a, mv);
        
        Matrix b = ds.getAll();
        assertNull("We shouldn't be getting any data from getAll() after a getAll()", b);
        
        Matrix c = ds.getNextBatch(1);
        assertNull("We shouldn't be getting any data from getNextBatch() after a getAll()", c);
    }
    
    public void testGetNextBatch() throws Exception {
        
        double[][] mv = new double[][] {
            {9, 8, 7},
            {1, 3, 5},
            {23, 25, 29}
        };
        
        
        File f = createTestDataSet(mv, ",");
        DelimitedTextFileDataSet ds = new DelimitedTextFileDataSet(f, ",");
        
        Matrix batch1 = ds.getNextBatch(1);
        assertMatrix(batch1, new double[][]{{9, 8, 7}});
        
        Matrix batch2 = ds.getNextBatch(1);
        assertMatrix(batch2, new double[][]{{1, 3, 5}});
        
        Matrix batch3 = ds.getNextBatch(1);
        assertMatrix(batch3, new double[][]{{23, 25, 29}});
        
        Matrix batch4 = ds.getNextBatch(1);
        assertNull("We shouldn't be getting any data from getNextBatch()", batch4);
    }
    
    public void testMixedGets() throws Exception {
        
        double[][] mv = new double[][] {
            {9, 8, 7},
            {1, 3, 5},
            {23, 25, 29}
        };
        
        
        File f = createTestDataSet(mv, ",");
        DelimitedTextFileDataSet ds = new DelimitedTextFileDataSet(f, ",");
        
        Matrix batch1 = ds.getNextBatch(2);
        assertMatrix(batch1, new double[][]{{9, 8, 7}, {1, 3, 5}});
        
        Matrix batch3 = ds.getAll();
        assertMatrix(batch3, new double[][]{{23, 25, 29}});
    }
    
    public void testMixedGetsWithBias() throws Exception {
        
        double[][] mv = new double[][] {
            {9, 8, 7},
            {1, 3, 5},
            {23, 25, 29}
        };
        
        
        File f = createTestDataSet(mv, ",");
        DelimitedTextFileDataSet ds = new DelimitedTextFileDataSet(f, ",", true);
        
        Matrix batch1 = ds.getNextBatch(2);
        assertMatrix(batch1, new double[][]{{1, 9, 8, 7}, {1, 1, 3, 5}});
        
        Matrix batch3 = ds.getAll();
        assertMatrix(batch3, new double[][]{{1, 23, 25, 29}});
    }
    
    public File createTestDataSet(double[][] values, String delimiter) throws Exception {
        File file = File.createTempFile(getClass().getSimpleName(), ".test");
        
        StringBuilder builder = new StringBuilder();
        for(int row = 0; row < values.length; row++) {
            for(int col = 0; col < values[row].length; col++) {
                builder.append(values[row][col]);
                if (col < values[row].length - 1) {
                    builder.append(delimiter);
                }
            }
            builder.append('\n');
        }
        
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(builder.toString().getBytes());
        fos.close();
        
        file.deleteOnExit();
        
        return file;
    }
    
    
    
    
    public void assertMatrix(Matrix m, double[][] values) {
        int rows = values.length;
        int cols = values[0].length;
        
        assertEquals("rows should be " + rows, rows, m.getRows());
        assertEquals("columns should be " + cols, cols, m.getColumns());
        
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                double val = values[row][col];
                assertEquals("row " + row + "," + col + " should be " + val, val, m.getValue(row, col));
            }
        }
    }
}
