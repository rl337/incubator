package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import org.rl337.skynet.types.Matrix;

public class MNISTPixelDataSet extends AbstractFileDataSet {
    private int mItems;
    private int mRows;
    private int mColumns;
    
    private int mLastRow;

    public MNISTPixelDataSet(File f, boolean addBias) throws IOException {
        super(f, addBias);
        mLastRow = 0;
    }
    
    public MNISTPixelDataSet(File f) throws IOException {
        this(f, false);
    }
    

    @Override
    protected void readHeaders(DataInput is) throws IOException {
        int magic = is.readInt();
        if (magic != 2051) {
            throw new IOException("IDX-3 file had bad magic number: " + magic);
        }
        
        mItems = is.readInt();
        mRows = is.readInt();
        mColumns = is.readInt();
    }
    
    
    public int rest() {
        return mItems - mLastRow;
    }

    @Override
    protected Matrix readNextBatch(DataInput is, int size) throws IOException {
        double[][] values = readRows(is, size);
        if (rest() < 1) {
            close();
        }
        return Matrix.matrix(values);
    }

    @Override
    protected Matrix readAll(DataInput is) throws IOException {
        double[][] values = readRows(is, -1);
        close();
        return Matrix.matrix(values);
    }
    
    public double[][] readRows(DataInput is, int size) throws IOException {
        
        int rest = rest();
        if (size == -1 || size > rest) {
            size = rest;
        }
        
        double[][] data = new double[size][mRows * mColumns];
        for (int row = 0; row < size; row++) {
            for(int col = 0; col < mRows * mColumns; col++) {
                data[row][col] = is.readUnsignedByte();
            }
        }
        return data;
    }

}
