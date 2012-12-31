package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import org.rl337.math.types.Matrix;

public class MNISTLabelDataSet extends AbstractFileDataSet {
    private int mItems;
    
    private int mLastRow;

    public MNISTLabelDataSet(File f) throws IOException {
        super(f);
        mLastRow = 0;
    }
    

    @Override
    protected void readHeaders(DataInput is) throws IOException {
        int magic = is.readInt();
        if (magic != 2049) {
            throw new IOException("IDX-1 file had bad magic number: " + magic);
        }
        mItems = is.readInt();
    }
    
    
    public int rest() {
        return mItems - mLastRow - 1;
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
        
        double[][] data = new double[size][1];
        for (int row = 0; row < size; row++) {
            data[row][0] = is.readByte();
        }
        return data;
    }

}
