package org.rl337.skynet.types;

import java.util.Random;

import org.rl337.skynet.types.Matrix;

import junit.framework.TestCase;

public class MatrixTest extends TestCase {

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
    
    
    public void testZeros() {
        Matrix m = Matrix.zeros(2, 3);
        assertMatrix(m, new double[][] {{0.0, 0.0, 0.0},{0.0,0.0,0.0}});
    }
    
    public void testIdentity() {
        Matrix m = Matrix.identity(3);
        assertMatrix(m, new double[][] {{1.0, 0.0, 0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}});
    }
    
    public void testMatrix() {
        Random rand = new Random();
        
        double[][] vals = new double[][] {
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()}
        };
        Matrix m = Matrix.matrix(vals);
        assertMatrix(m, vals);
    }
    
    public void testVector() {
        Random rand = new Random();
        
        double[][] vals = new double[][] {
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()}
        };
        Matrix m = Matrix.vector(vals[0]);
        assertMatrix(m, new double[][] {{vals[0][0]},{vals[0][1]},{vals[0][2]}});
    }
    
    public void testEquals() {
        Random rand = new Random();
        double[][] vals1 = new double[][] {
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()}
        };
        
        double[][] vals2 = new double[][] {
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()},
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()}
        };
        
        Matrix m1 = Matrix.matrix(vals1);
        Matrix m2 = Matrix.matrix(vals1);
        
        Matrix m3 = Matrix.matrix(vals2);
        
        assertTrue("Two identical matrices should be equal",m1.equals(m2));
        assertTrue("equality should go both ways",m2.equals(m1));
        assertTrue("A matrix should be equal to itself", m1.equals(m2));
        
        assertFalse("These matrices are different", m3.equals(m1));
        // These are here primarily to make sure we don't throw on different size compares
        assertFalse("These matrices have different rowcount", m3.equals(Matrix.zeros(2, 3)));
        assertFalse("These matrices have different colcount", m3.equals(Matrix.zeros(3, 2)));
    }
    
    public void testTranspose() {
        Random rand = new Random();
        double[][] vals = new double[][] {
            {rand.nextDouble(), rand.nextDouble(), rand.nextDouble()}
        };
        double[][] valstranspose = new double[][] {{vals[0][0]},{vals[0][1]},{vals[0][2]}};

        Matrix m = Matrix.matrix(vals);
        
        assertMatrix(m.transpose(), valstranspose);
        assertMatrix(m.transpose().transpose(), vals);
        
    }
    
    public void testIdentityMultiply() {
        Matrix id = Matrix.identity(5);
        double[] allones = new double[] {1.0, 1.0, 1.0, 1.0, 1.0};
        Matrix vector = Matrix.vector(allones);
        
        Matrix result = vector.transpose().multiply(id);
        assertEquals("anything times a identity should be itself", vector, result);
    }

    public void testMultiply() {
        double[][] vals1 = new double[][] {
            {1, 2, 3},
            {4, 5, 6},
        };
        
        double[][] vals2 = new double[][] {
            {7, 8},
            {9,10},
            {11,12}
        };
        
        double[][] resultvals = new double[][] {
            {58, 64},
            {139,154},
        };
        
        Matrix a = Matrix.matrix(vals1);
        Matrix b = Matrix.matrix(vals2);
        
        //Matrix c = Matrix.matrix(resultvals);
        //System.out.println(a.multiply(b));
        //System.out.println(c);
        
        assertMatrix(a.multiply(b), resultvals);
    }
    
    public void testAdd() {
        double[][] avals = new double[][] {
            {1, 2},
            {3, 4},
        };
        double[][] bvals = new double[][] {
            {5, 6},
            {7, 8},
        };
        
        double[][] resultvals = new double[][] {
            {6, 8},
            {10,12},
        };

        Matrix a = Matrix.matrix(avals);
        Matrix b = Matrix.matrix(bvals);
        
        assertMatrix(a.add(b), resultvals);
    }
    
    public void testMultiplyElementWise() {
        double[][] avals = new double[][] {
            {1, 2},
            {3, 4},
        };
        double[][] bvals = new double[][] {
            {5, 6},
            {7, 8},
        };
        
        double[][] resultvals = new double[][] {
            {5, 12},
            {21,32},
        };

        Matrix a = Matrix.matrix(avals);
        Matrix b = Matrix.matrix(bvals);
        
        assertMatrix(a.multiplyElementWise(b), resultvals);
    }
    
    public void testSum() {
        double[][] avals = new double[][] {
            {1, 2},
            {3, 4},
        };
        
        Matrix m = Matrix.matrix(avals);
        
        assertEquals("sum sohuld be 10", 10.0, m.sum());
    }
    
    public void testSumRows() {
        double[][] avals = new double[][] {
            {1, 2},
            {3, 4},
        };
        double[][] resultvals = new double[][] {
            {4, 6},
        };
        
        Matrix m = Matrix.matrix(avals);
        
        assertMatrix(m.sumRows(), resultvals);
    }
    
    public void testSliceColumn() {
        double[][] avals = new double[][] {
            {1, 2},
            {3, 4},
        };
        double[][] resultvals = new double[][] {
            {2},
            {4}
        };
        
        Matrix m = Matrix.matrix(avals);
        
        assertMatrix(m.sliceColumn(1), resultvals);
    }
    
    public void testAppendColumns() {
        double[][] avals = new double[][] {
                {1, 2},
                {3, 4},
                {5, 6}
            };
        double[][] resultvals = new double[][] {
            {1, 2, 1, 2},
            {3, 4, 3, 4},
            {5, 6, 5, 6}
        };
        
        Matrix m = Matrix.matrix(avals);
        
        assertMatrix(m.appendColumns(m), resultvals);
    }
    
    public void testAppendColumns2() {
        double[][] avals = new double[][] {
                {1},
                {3},
                {5}
            };
        double[][] bvals = new double[][] {
                {1, 2},
                {3, 4},
                {5, 6}
            };
        double[][] resultvals = new double[][] {
            {1, 1, 2},
            {3, 3, 4},
            {5, 5, 6}
        };
        
        Matrix m1 = Matrix.matrix(avals);
        Matrix m2 = Matrix.matrix(bvals);
        assertMatrix(m1.appendColumns(m2), resultvals);
    }
    
    public void testAppendRows() {
        double[][] avals = new double[][] {
                {1, 2, 3},
                {3, 4, 5},
            };
        double[][] resultvals = new double[][] {
            {1, 2, 3},
            {3, 4, 5},
            {1, 2, 3},
            {3, 4, 5},
        };
        
        Matrix m = Matrix.matrix(avals);
        assertMatrix(m.appendRows(m), resultvals);
    }
    
    public void testAppendRows2() {
        double[][] avals = new double[][] {
                {1, 2, 3},
            };
        
        double[][] bvals = new double[][] {
                {1, 2, 3},
                {3, 4, 5},
            };
        
        double[][] resultvals = new double[][] {
            {1, 2, 3},
            {1, 2, 3},
            {3, 4, 5},
        };
        
        Matrix m1 = Matrix.matrix(avals);
        Matrix m2 = Matrix.matrix(bvals);
        assertMatrix(m1.appendRows(m2), resultvals);
    }
    
    public void testMean() {
        double[][] bvals = new double[][] {
                {1, 2, 3},
                {3, 4, 5},
            };
        
        Matrix b = Matrix.matrix(bvals);
        
        assertEquals(3.0, b.mean());
    }
    
}
