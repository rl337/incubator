package org.rl337.skynet.datasets;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.rl337.skynet.DataSet;
import org.rl337.skynet.types.Matrix;

public abstract class AbstractFileDataSet implements DataSet {
    private DataInputStream mInput;
    private boolean mEOF;
    
    protected AbstractFileDataSet(File f) throws IOException {
        mInput = getInputStream(f);
        mEOF = false;
    }
    
    private DataInputStream getInputStream(File file) throws IOException {
        String filename = file.getAbsolutePath();
        
        InputStream inputStream = new FileInputStream(file);
        if (filename.endsWith(".zip")) {
            inputStream = new ZipInputStream(inputStream);
        } else if (filename.endsWith(".gz")) {
            inputStream = new GZIPInputStream(inputStream);
        }
        
        DataInputStream result = new DataInputStream(inputStream);
        
        readHeaders(result);
        
        return result;
    }
    
    protected abstract void readHeaders(DataInput is) throws IOException;
    protected abstract Matrix readNextBatch(DataInput is, int size) throws IOException;
    protected abstract Matrix readAll(DataInput is) throws IOException;

    public Matrix getNextBatch(int size) {
        try {
            return readNextBatch(mInput, size);
        } catch (IOException e) {
            close();
            return null;
        }
    }

    public Matrix getAll() {
        try {
            return readAll(mInput);
        } catch (EOFException e) {
            mEOF = true;
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            close();
        }
    }
    
    public void close() {
        try {
            mEOF = true;
            mInput.close();
        } catch (IOException e) {
        }
    }

    public boolean hasMore() {
        return !mEOF;
    }
}
