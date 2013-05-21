package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.rl337.math.types.Matrix;

public class MNISTLabelDataSet extends AbstractFileDataSet {
    private int mItems;
    
    private int mLastRow;

    public MNISTLabelDataSet(File f) throws IOException {
        super(f);
        mLastRow = 0;
    }
    public MNISTLabelDataSet(File f, Random rand) throws IOException {
        super(f, rand);
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
        double[][] values = readRows(is, size, 1.0);
        if (rest() < 1) {
            close();
        }
        return Matrix.matrix(values);
    }

    @Override
    protected Matrix readAll(DataInput is) throws IOException {
        double[][] values = readRows(is, -1, 1.0);
        close();
        return Matrix.matrix(values);
    }
    
    public double[][] readRows(DataInput is, int size, double subsample) throws IOException {
        
        int rest = rest();
        if (size == -1 || size > rest) {
            size = rest;
        }
        
        ArrayList<Double> data = new ArrayList<Double>();
        for (int row = 0; row < size; row++) {
            double b = is.readByte(); 
            if (shouldKeep(subsample)) {
                data.add(b);
            }
        }
        
        double[][] result = new double[data.size()][1];
        for(int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        
        return result;
    }


    @Override
    protected Matrix readAllSubsampled(DataInput is, double prob) throws IOException {
        double[][] values = readRows(is, -1, prob);
        close();
        return Matrix.matrix(values);
    }

}
