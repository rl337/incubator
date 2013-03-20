package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.rl337.math.types.Matrix;

public class DelimitedTextFileDataSet extends AbstractFileDataSet {
    private String mDelimiter;
    private int mColumnCount;
    private long mSkipLines;

    public DelimitedTextFileDataSet(File f, String delimiter, long skipLines, boolean addBias) throws IOException {
        super(f, addBias, null);
        mDelimiter = delimiter;
        mColumnCount = 0;
        mSkipLines = skipLines;
    }
    
    public DelimitedTextFileDataSet(File f, String delimiter, boolean addBias) throws IOException {
        this(f, delimiter, 0, addBias);
    }
    
    public DelimitedTextFileDataSet(File f, String delimiter) throws IOException {
        this(f, delimiter, 0, false);
    }
    
    public DelimitedTextFileDataSet(File f, String delimiter, long skipLines) throws IOException {
        this(f, delimiter, skipLines, false);
    }
    
    @Override
    protected void readHeaders(DataInput is) throws IOException {
    }

    @Override
    protected Matrix readNextBatch(DataInput is, int size) throws IOException {
        ArrayList<double[]> rows = readRows(is, size, 1.0);
        if (rows.size() < 1) {
            close();
            return null;
        };
        double[][] rawValues = rows.toArray(new double[rows.size()][mColumnCount]);
        return Matrix.matrix(rawValues);
    }

    @Override
    protected Matrix readAll(DataInput is) throws IOException {
        ArrayList<double[]> rows = readRows(is, -1, 1.0);
        if (rows.size() < 1) { 
            close();
            return null;
        };
        double[][] rawValues = rows.toArray(new double[rows.size()][mColumnCount]);
        return Matrix.matrix(rawValues);
    }

    public ArrayList<double[]> readRows(DataInput is, int size, double sampleProb) throws IOException {
        
        while(mSkipLines > 0) {
            is.readLine();
            mSkipLines--;
        }
        
        ArrayList<double[]> rows = new ArrayList<double[]>();
        
        int read = 0;
        for(String val = is.readLine(); val != null; val = is.readLine()) {
            if (!shouldKeep(sampleProb)) {
                continue;
            }
            
            String[] parts = val.split(mDelimiter);
            if (mColumnCount == 0) {
                mColumnCount = parts.length;
            }
            if (parts.length != mColumnCount) {
                throw new IOException("Invalid column count. Found: " + parts.length + " expected: " + mColumnCount);
            }
            
            double[] vals = new double[mColumnCount];
            for (int i = 0; i < vals.length; i++) {
                try {
                    vals[i] = Double.parseDouble(parts[i]);
                } catch (NumberFormatException e) {
                    vals[i] = 0.0;
                }
            }
            rows.add(vals);
            
            read++;
            if (size != -1 && read >= size) {
                break;
            }
        }
        return rows;
    }

    @Override
    protected Matrix readAllSubsampled(DataInput is, double prob) throws IOException {
        ArrayList<double[]> rows = readRows(is, -1, prob);
        if (rows.size() < 1) { 
            close();
            return null;
        };
        double[][] rawValues = rows.toArray(new double[rows.size()][mColumnCount]);
        return Matrix.matrix(rawValues);
    }
}
