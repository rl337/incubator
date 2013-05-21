package org.rl337.math;

import org.rl337.math.types.spreadsheet.Cell;

public class Spreadsheet {
    private Cell[] mCells;
    private int mRows;
    private int mColumns;
    
    public Spreadsheet(int rows, int cols) {
        mCells = new Cell[rows * cols];
        mRows = rows;
        mColumns = cols;
    }
    
    private int getIndex(int row, int col) {
        return row * mColumns + col;
    }
    
    public String getRawValue(int row, int col) {
        return mCells[getIndex(row, col)].getRawValue();
    }
    
    public void setRawValue(int row, int col, String val) {
        mCells[getIndex(row, col)].setRawValue(val);
    }
    
    public String getStringValue(int row, int col) {
        return mCells[getIndex(row, col)].getStringValue();
    }
    
    public double getDoubleValue(int row, int col) {
        return mCells[getIndex(row, col)].getDoubleValue();
    }
    
    public int getRows() {
        return mRows;
    }
    
    public int getColumns() {
        return mColumns;
    }

}
