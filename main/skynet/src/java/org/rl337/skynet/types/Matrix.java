package org.rl337.skynet.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Matrix {
    private int mColumns;
    private int mRows;
    
    private double[] mValues;
    
    private Matrix(int rows, int columns) {
        mColumns = rows;
        mRows = columns;
        
        mValues = new double[rows * columns];
    }
    
    public int getColumns() { return mColumns; }
    public int getRows() { return mRows; }
    
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
    
    public String toDimensionString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mRows).append(" row(s) x ").append(mColumns).append(" column(s)");
        
        return builder.toString();
    }
    
    public Matrix transpose() {
        Matrix m = zeros(mColumns, mRows);
        for(int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mRows; j++) {
                m.setValue(i, j, getValue(j, i));
            }
        }
        
        return m;
    }
    
    public Matrix multiply(Matrix m) {
        Matrix newM = zeros(m.getColumns(), getRows());
        if (mColumns != m.mRows) {
            throw new IllegalArgumentException("Tried to multiply " + toDimensionString() + " by " + m.toDimensionString());
        }
        
        for(int i = 0; i < mRows; i++) {
            for(int j = 0; j < m.getColumns(); j++) {
                double value = 0;
                for(int k = 0; k < mColumns; k++){
                    value += getValue(i,k) * m.getValue(k,j);
                }
                newM.setValue(i,j,value);
            }  
        }
        
        return newM;
    }
    
    public Matrix multiply(double s) {
        
        Matrix r = zeros(mRows, mColumns);
        for(int i = 0; i < mColumns; i++) {
            for(int j = 0; j < mRows; j++) {
                r.setValue(j, i, getValue(j, i) * s);
            }
        }
        
        return r;
    }
    
    public Matrix divide(double s) {
        
        Matrix r = zeros(mRows, mColumns);
        for(int i = 0; i < mColumns; i++) {
            for(int j = 0; j < mRows; j++) {
                r.setValue(j, i, getValue(j, i) / s);
            }
        }
        
        return r;
    }
    
    public Matrix add(Matrix m) {
        return elementWiseOperation("add", this, m,
            new MatrixOperation() {
                public double operation(double aVal, double bVal) {
                    return aVal + bVal;
                }
            }
        );
    }
    
    public Matrix subtract(Matrix m) {
        return elementWiseOperation("subtract", this, m,
            new MatrixOperation() {
                public double operation(double aVal, double bVal) {
                    return aVal - bVal;
                }
            }
        );
    }
    
    public Matrix multiplyElementWise(Matrix m) {
        return elementWiseOperation("multiply", this, m,
            new MatrixOperation() {
                public double operation(double aVal, double bVal) {
                    return aVal * bVal;
                }
            }
        );
    }
    
    public double sum() {
        double r = 0.0;
        for(int i = 0; i < mColumns; i++) {
            for(int j = 0; j < mRows; j++) {
                r += getValue(j, i);
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
        
        for(int i = 0; i < mColumns; i++) {
            result.setValue(0, i, getValue(row, i));
        }
        
        return result;
    }

    public Matrix sliceColumn(int col) {
        Matrix result = zeros(mRows, 1);
        
        for(int i = 0; i < mRows; i++) {
            result.setValue(i, 0, getValue(i, col));
        }
        
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
    
    public static Matrix zeros(int rows, int columns) {
        return new Matrix(columns, rows);
    }
    
    public static Matrix matrix(int rows, int cols, double value) {

        Matrix m = zeros(rows, cols);
        
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                m.setValue(i, j, value);
            }
        }
        
        return m;
    }
    
    public static Matrix matrix(double[][] values) {
        int rows = values.length;
        int cols = values[0].length;
        Matrix m = zeros(rows, cols);
        
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                m.setValue(i, j, values[i][j]);
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
    
    public double mean() {
        
        double total = 0;
        for (int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mRows; j++) {
                total += getValue(j, i);
            }
        }
        
        return total / (mRows * mColumns);
    }
    
    public static Matrix loadFile(File f) throws IOException {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        
        int colcount = 0;
        ArrayList<double[]> rows = new ArrayList<double[]>();
        for(String val = br.readLine(); val != null; val = br.readLine()) {
            String[] parts = val.split(",");
            if (colcount == 0) {
                colcount = parts.length;
            }
            double[] vals = new double[colcount];
            for (int i = 0; i < vals.length; i++) {
                vals[i] = Double.parseDouble(parts[i]);
            }
            rows.add(vals);
        }

        double[][] rawValues = rows.toArray(new double[rows.size()][colcount]);
        return Matrix.matrix(rawValues);
    }
    
    private static void copyRegion(Matrix src, int srcColumn, int srcRow, Matrix target, int targetColumn, int targetRow, int columns, int rows) {
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                target.setValue(targetRow + j, targetColumn + i, src.getValue(srcRow + j, srcColumn + i));
            }
        }
    }
    
    private static Matrix elementWiseOperation(String operationName, Matrix a, Matrix b, MatrixOperation op) {
        a.assertDimensions(operationName, true, true, b);
        
        int rows = a.getRows();
        int columns = a.getColumns();
        
        Matrix r = zeros(rows, columns);
        
        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {
                r.setValue(j, i, op.operation(a.getValue(j, i), b.getValue(j, i)));
            }
        }
        
        return r;
    }
    
    private static interface MatrixOperation {
        double operation(double aVal, double bVal);
    }

}
