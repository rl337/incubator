package org.rl337.math.types;

import java.util.Random;

/**
 * The Matrix class is a wrapper around an array with convenience methods which
 * simulate a 2-dimensional space.  The point of this class is to provide an 
 * easy way to simulate, in java, a lot of the functionality you might find
 * in Math packages like r and octave.
 * 
 * This implementation was not designed to be particularly efficient.  Almost all
 * operations which return a Matrix object will return a new instance rather than
 * re-use the existing object.
 * 
 * The constructor for Matrix is private on purpose. This requires you to code using
 * the various static methods which have much better descriptions of the type of matrix
 * they return.  For example:
 * 
 * Matrix.identity(int n) returns an nxn matrix with 1s along the diagonal.
 * 
 * There are two sub-interfaces of Matrix which define how to do arbitrary binary element-wise
 * operations as well as unary matrix transforms.
 * 
 * For unary operations, use the MatrixOperation interface
 * This code multiplies all elements of a random matrix by 3
 * 
 *   Matrix m = Matrix.random(3, 3);
 *   Matrix newMatrix = Matrix.matrixOperation(m, new MatrixOperation() {
 *      public double operation(int row, int col, double val) {
 *          return val * 3;
 *      }
 *   });
 *   
 * For binary element-wise operations use the MatrixElementWiseOperation.  This code does
 * returns the matrix with the element-wise max of two random matrices.
 * 
 *   Matrix a = Matrix.random(3, 3);
 *   Matrix b = Matrix.random(3, 3);
 *   Matrix newMatrix = Matrix.elementWiseOperation("max", a, b,
 *      new MatrixElementWiseOperation() {
 *          public double operation(int row, int col, double aVal, double bVal) {
 *              if (aVal > bVal) return aVal;
 *              return bVal;
 *          }
 *      }
 *  );
 *  
 *  Here, the operation name helps with debugging.  If some matrix size assertion fails,
 *  you'll not only be given the resulting exception stacktrace, but the actual operation
 *  that failed.
 * 
 * @author Richard Lee
 */
public class Matrix {
    private int mColumns;
    private int mRows;
    
    private double[] mValues;
    
    /**
     * Basic matrix constructor.  Access to this method is purposely restricted to 
     * require users to choose more descriptive construction methods below.
     * 
     * @param rows number of rows of the matrix
     * @param columns number of columns each row contains
     */
    private Matrix(int rows, int columns) {
        mColumns = rows;
        mRows = columns;
        
        mValues = new double[rows * columns];
    }
    
    /**
     * Returns the number of columns of this Matrix.
     * @return the number of columns as an integer
     */
    public int getColumns() { return mColumns; }
    
    /**
     * Return the number of rows of this Matrix
     * @return the number of rows as an integer
     */
    public int getRows() { return mRows; }
    
    /**
     * Returns a formatted string value of the contents of this matrix.
     * The output is comma separated for columns and newline delimited
     * for rows.  For example, a 3x3 identity matrix would result in
     * the following string:
     * 
     * <pre>
     * 1.0,0.0,1.0
     * 0.0,1.0,0.0
     * 0.0,0.0,1.0
     * </pre>
     * 
     * @return A comma delimited representation of the matrix contents
     */
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < mRows; i++) {
            for(int j = 0; j < mColumns; j++) {
                builder.append(mValues[i * mColumns + j]);
                builder.append(",");
            }
            builder.setLength(builder.length() - 1);
            builder.append('\n');
        }
        
        return builder.toString();
    }
    
    /**
     * Returns a human readable string describing the size of the matrix.
     * A 3x5 matrix will return the following:
     * 
     * <code>
     * 3 rows(s) x 5 column(s)
     * </code>
     * 
     * @return a string describing the dimensions of the matrix
     */
    public String toDimensionString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mRows).append(" row(s) x ").append(mColumns).append(" column(s)");
        
        return builder.toString();
    }
    
    /**
     * This creates a new matrix whose contents is the transpose of this matrix.
     * A transpose is simply the rows and columns reversed
     * @return A matrix representing the transpose of this matrix
     */
    public Matrix transpose() {
        Matrix m = zeros(mColumns, mRows);
        for(int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mRows; j++) {
                m.setValue(i, j, getValue(j, i));
            }
        }
        
        return m;
    }
    
    /**
     * This creates a matrix object that is the Matrix Product of this matrix
     * and the matrix specified by m.  Specifically, if this matrix is A and
     * matrix passed as m is B, this returns A x B.
     * 
     * @param m the matrix to multiply by
     * @return A matrix representing the matrix product of this x m
     */
    public Matrix multiply(Matrix m) {
        if (mColumns != m.mRows) {
            throw new IllegalArgumentException("Tried to multiply " + toDimensionString() + " by " + m.toDimensionString());
        }
        
        Matrix newM = zeros(getRows(), m.getColumns());
        for(int i = 0; i < mRows; i++) {
            for(int j = 0; j < m.getColumns(); j++) {
                double value = 0.0;
                for(int k = 0; k < mColumns; k++){
                    double a = getValue(i, k);
                    double b = m.getValue(k, j);
                    // Java thinks that -Inf * 0 is NaN, so if either are 0, use 0.
                    if (a == 0.0 || b == 0.0) {
                        continue;
                    }
                    
                    value += getValue(i,k) * m.getValue(k,j);
                }
                newM.setValue(i,j,value);
            }  
        }
        
        return newM;
    }
    
    /**
     * This creates a matrix whose elements are the value of the current
     * Matrix's elements multiplied by the scalar value s.
     * @param s the value to multiply this matrix's elements by
     * @return a matrix with values equal to this matrix's values scaled by s.
     */
    public Matrix multiply(final double s) {
        return matrixOperation(this, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                if (s == 0.0) {
                    return 0.0;
                }
                
                return val * s;
            }
        });
    }
    
    /**
     * This creates a matrix whose elements are the value of the current
     * Matrix's elements divided by some scalar value s. 
     * @param s the value to divide this matrix's elements by
     * @return a matrix with values equal to this matrix's values normalized by s
     */
    public Matrix divide(final double s) {
        return matrixOperation(this, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                if (val == 0.0) {
                    return 0.0;
                }
                return val / s;
            }
        });
    }
    
    /**
     * This creates a matrix whose elements are the element-wise sum of this
     * matrix and matrix m.  Note that this matrix and matrix m must be the
     * same size.
     * 
     * @param m the matrix to add this matrix to.
     * @return A matrix that is the sum of this matrix and m
     */
    public Matrix add(Matrix m) {
        return elementWiseOperation("add", this, m,
            new MatrixElementWiseOperation() {
                public double operation(int row, int col, double aVal, double bVal) {
                    return aVal + bVal;
                }
            }
        );
    }
    
    /**
     * This creates a matrix whose elements are the element-wise difference of this
     * matrix and matrix m.  Note that this matrix and matrix m must be the
     * same size.
     * 
     * Specifically this calculates: this - m
     * 
     * @param m the matrix to subtract from this matrix to.
     * @return A matrix that is the difference of this matrix and m
     */
    public Matrix subtract(Matrix m) {
        return elementWiseOperation("subtract", this, m,
            new MatrixElementWiseOperation() {
                public double operation(int row, int col, double aVal, double bVal) {
                    return aVal - bVal;
                }
            }
        );
    }
    
    public Matrix multiplyElementWise(Matrix m) {
        return elementWiseOperation("multiplyElementWise", this, m,
            new MatrixElementWiseOperation() {
                public double operation(int row, int col, double aVal, double bVal) {
                    if (aVal == 0.0 || bVal == 0.0) {
                        return 0.0;
                    }
                    return aVal * bVal;
                }
            }
        );
    }

    
    public Matrix add(final double v) {
        return matrixOperation(this,
            new MatrixOperation() {
                public double operation(int row, int col, double val) {
                    return val + v;
                }
            }
        );
    }
    

    public Matrix pow(final double i) {
        return matrixOperation(this,
            new MatrixOperation() {
                public double operation(int row, int col, double val) {
                    return Math.pow(val, i);
                }
            }
        );
    }
    
    public double sum() {
        double r = 0.0;
        
        for(int i = 0; i < mColumns; i++) {
            for(int j = 0; j < mRows; j++) {
                r += getValue(j,  i);
            }
        }
        
        return r;
    }
    
    public Matrix sumRows() {
        Matrix r = Matrix.zeros(1, mColumns);
        
        for(int i = 0; i < mColumns; i++) {
            double colVal = 0;
            for(int j = 0; j < mRows; j++) {
                colVal += getValue(j, i);
            }
            r.setValue(0, i, colVal);
        }
        
        return r;
    }
    
    public Matrix sumColumns() {
        Matrix r = Matrix.zeros(mRows, 1);
        
        for(int i = 0; i < mRows; i++) {
            double rowVal = 0;
            for(int j = 0; j < mColumns; j++) {
                rowVal += getValue(i, j);
            }
            r.setValue(i, 0, rowVal);
        }
        
        return r;
    }
    
    public boolean equals(Object o) {
        if (! (o instanceof Matrix)) {
            return false;
        }
        
        Matrix m = (Matrix) o;
        if (mColumns != m.getColumns()) {
            return false;
        }
        
        if (mRows != m.getRows()) {
            return false;
        }
        
        for (int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mRows; j++) {
                if (getValue(j, i) != m.getValue(j, i)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public int hashCode() {
        return toString().hashCode();
    }
    
    public double getValue(int row, int col) {
        return mValues[row * mColumns + col];
    }
    
    public void setValue(int row, int col, double value) {
        mValues[row * mColumns + col] = value;
    }

    private void assertDimensions(String operation, boolean rows, boolean columns, Matrix m) {
        if (rows && mRows != m.getRows()) {
            throw new IllegalArgumentException("Mismatched rows in " + operation + ": " + "a:" + toDimensionString() + " b:" + m.toDimensionString());
        }
        
        if (columns && mColumns != m.getColumns()) {
            throw new IllegalArgumentException("Mismatched columns in " + operation + ": " + "a:" + toDimensionString() + " b:" + m.toDimensionString());
        }
    }
    
    public Matrix sliceRow(int row) {
        Matrix result = zeros(1, mColumns);
        
        copyRegion(this, 0, row, result, 0, 0, mColumns, 1);
        
        return result;
    }
    
    
    public Matrix sliceRows(int startRow, int stopRow) {
        int rows = stopRow - startRow + 1;
        Matrix result = zeros(rows, mColumns);
        
        copyRegion(this, 0, startRow, result, 0, 0, mColumns, rows);
        
        return result;
    }

    public Matrix sliceColumn(int col) {
        Matrix result = zeros(mRows, 1);
        
        copyRegion(this, col, 0, result, 0, 0, 1, mRows);
        
        return result;
    }
    
    public Matrix sliceColumns(int startColumn, int stopColumn) {
        int columns = stopColumn - startColumn + 1;
        Matrix result = zeros(mRows, columns);
        
        copyRegion(this, startColumn, 0, result, 0, 0, columns, mRows);
        
        return result;
    }

    public Matrix appendColumns(Matrix m) {
        assertDimensions("appendColumns", true, false, m);
        
        Matrix result = zeros(mRows, mColumns + m.mColumns);
        copyRegion(this, 0, 0, result, 0, 0, mColumns, mRows);
        copyRegion(m, 0, 0, result, mColumns, 0, m.mColumns, m.mRows);
        
        return result;
    }
    
    public Matrix appendRows(Matrix m) {
        assertDimensions("appendRows", false, true, m);
        
        Matrix result = zeros(mRows + m.mRows, mColumns);
        copyRegion(this, 0, 0, result, 0, 0, mColumns, mRows);
        copyRegion(m, 0, 0, result, 0, mRows, m.mColumns, m.mRows);
        
        return result;
    }
    
    public double mean() {
        return sum() / (mRows * mColumns);
    }
    
    public Stats stats() {
        double elements = mRows * mColumns;

        double max = getValue(0, 0);
        double min = max;
        
        double variance = 0;
        double mean = 0;
        for (int j = 0; j < mRows; j++) {
            for (int i = 0; i < mColumns; i++) {
                double value = getValue(j, i);
                
                if (value > max) { max = value; }
                if (value < min) { min = value; }
                
                variance += (value * value) / elements;
                mean += value / elements;
            }
        }
        
        double stddev = Math.sqrt(variance - mean * mean);
        return new Stats(mean, stddev, variance, max, min);
    }
    
    /**
     * Returns a matrix of zeros.
     * @param rows number of rows
     * @param columns number of columns
     * @return A matrix of zeros whose size is rows x columns
     */
    public static Matrix zeros(int rows, int columns) {
        return new Matrix(columns, rows);
    }
    
    /**
     * Returns a matrix of a specific value.
     * @param rows number of rows
     * @param columns number of columns
     * @param value the value to fill the matrix with
     * @return A matrix of some value whose size is rows x columns
     */
    public static Matrix matrix(int rows, int cols, double value) {

        Matrix m = zeros(rows, cols);
        
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                m.setValue(i, j, value);
            }
        }
        
        return m;
    }
    
    /**
     * Returns a matrix of ones.
     * @param rows number of rows
     * @param columns number of columns
     * @return A matrix of ones whose size is rows x columns
     */
    public static Matrix ones(int rows, int cols) {
        return matrix(rows, cols, 1.0);
    }
    
    public static Matrix random(int rows, int cols, final double min, double max, final Random random) {
         Matrix m = zeros(rows, cols);
         
         final double range = max - min;
         return matrixOperation(m, new MatrixOperation() {
            public double operation(int row, int col, double val) {
                return random.nextDouble() * range + min;
            }
         });
    }
    
    public static Matrix random(int rows, int cols, final double min, double max) {
        return random(rows, cols, min, max, new Random());
    }
    
    public static Matrix random(int rows, int cols) {
        return random(rows, cols, 0.0, 1.0);
    }
    
    public static Matrix matrix(double[][] values) {
        return matrix(values, 0, values.length);
    }
    
    public static Matrix matrix(Matrix x) {
        Matrix result = Matrix.zeros(x.getRows(), x.getColumns());
        
        for(int i = 0; i < x.mValues.length; i++) {
            result.mValues[i] = x.mValues[i];
        }
        
        return result;
    }
    
    public static Matrix matrix(double[][] values, int startRow, int maxRows) {
        int rows = maxRows;
        int cols = values[0].length;
        Matrix m = zeros(rows, cols);
        
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                m.setValue(i, j, values[i + startRow][j]);
            }
        }
        
        return m;
    }
    
    public static Matrix vector(double values[]) {
        int rows = values.length;
        
        Matrix m = zeros(rows, 1);
        for(int i = 0; i < rows; i++) {
            m.setValue(i,  0, values[i]);
        }
        
        return m;
    }
    
    
    public static Matrix identity(int size) {
        Matrix m = zeros(size, size);
        
        for(int i = 0; i < size; i++) {
            m.setValue(i, i, 1);
        }
        
        return m;
    }
    
    private static void copyRegion(Matrix src, int srcColumn, int srcRow, Matrix target, int targetColumn, int targetRow, int columns, int rows) {
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                target.setValue(targetRow + j, targetColumn + i, src.getValue(srcRow + j, srcColumn + i));
            }
        }
    }
    
    public static Matrix elementWiseOperation(String operationName, Matrix a, Matrix b, MatrixElementWiseOperation op) {
        a.assertDimensions(operationName, true, true, b);
        
        int rows = a.getRows();
        int columns = a.getColumns();
        
        Matrix r = zeros(rows, columns);
        
        for(int j = 0; j < rows; j++) {
            for(int i = 0; i < columns; i++) {
                r.setValue(j, i, op.operation(j, i, a.getValue(j, i), b.getValue(j, i)));
            }
        }
        
        return r;
    }
    
    public static Matrix matrixOperation(Matrix a, MatrixOperation op) {
        int rows = a.getRows();
        int columns = a.getColumns();
        
        Matrix r = zeros(rows, columns);
        
        for(int j = 0; j < rows; j++) {
            for(int i = 0; i < columns; i++) {
                r.setValue(j, i, op.operation(j, i, a.getValue(j, i)));
            }
        }
        
        return r;
    }
    
    public static interface MatrixElementWiseOperation {
        double operation(int row, int col, double aVal, double bVal);
    }
    
    public static interface MatrixOperation {
        double operation(int row, int col, double val);
    }
    
    public static class Stats {
        public final double mean;
        public final double deviation;
        public final double variance;
        public final double max;
        public final double min;
        
        public Stats(double mean, double deviation, double variance, double max, double min) {
            this.mean = mean;
            this.deviation = deviation;
            this.variance = variance;
            this.max = max;
            this.min = min;
        }
    }


}
