package org.rl337.skynet.datasets;

import org.rl337.skynet.DataSet;
import org.rl337.math.types.Matrix;

public class FilterDataSet implements DataSet {
    private DataSet mDataSet;
    private Filter mFilter;
    
    public FilterDataSet(DataSet wrapped, Filter filter) {
        mDataSet = wrapped;
        mFilter = filter;
    }
    

    public Matrix getNextBatch(int size) {
        if (!hasMore()) {
            return null;
        }
        
        if (size < 1) {
            return null;
        }
        
        Matrix result = mDataSet.getNextBatch(1);
        if (result == null) {
            return null;
        }
        
        int count = 1;
        while(hasMore() && count < size) {
            Matrix m = mDataSet.getNextBatch(1);
            if (m == null) {
                break;
            }
            
            if (!mFilter.valid(m)) {
                continue;
            }
            
            result.appendRows(m);
            count++;
        }
        
        return result;
    }

    public Matrix getAll() {
        if (!hasMore()) {
            return null;
        }
        
        Matrix result = mDataSet.getNextBatch(1);
        if (result == null) {
            return null;
        }
        
        while(hasMore()) {
            Matrix m = mDataSet.getNextBatch(1);
            if (m == null) {
                break;
            }
            
            if (!mFilter.valid(m)) {
                continue;
            }
            
            result.appendRows(m);
        }
        
        return result;
    }

    public boolean hasMore() {
        return mDataSet.hasMore();
    }
    
    public static interface Filter {
        boolean valid(Matrix row);
    }

}
