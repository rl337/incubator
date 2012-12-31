package org.rl337.skynet.datasets;

import org.rl337.skynet.DataSet;
import org.rl337.math.types.Matrix;

public class MatrixDataSet implements DataSet {
        private Matrix mMatrix;
        private int mLastRow;
        
        public MatrixDataSet(Matrix m) {
            mMatrix = m;
            mLastRow = 0;
        }
        
        public int rest() {
            return mMatrix.getRows() - mLastRow;
        }
        
        public boolean hasMore() {
            return rest() > 0;
        }

        public Matrix getNextBatch(int size) {
            if (!hasMore()) {
                return null;
            }
            
            int rest = rest();
            if (rest < size) {
                size = rest;
            }
            
            Matrix result = mMatrix.sliceRows(mLastRow, mLastRow + size - 1);
            mLastRow += size;
            
            return result;
        }

        public Matrix getAll() {
            if (!hasMore()) {
                return null;
            }
            
            return getNextBatch(rest());
        }
}
