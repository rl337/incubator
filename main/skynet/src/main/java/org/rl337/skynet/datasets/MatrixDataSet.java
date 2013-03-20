package org.rl337.skynet.datasets;

import org.rl337.math.types.Matrix;

public class MatrixDataSet extends AbstractDataSet {
        private Matrix mMatrix;
        private int mLastRow;
        
        public MatrixDataSet(Matrix m) {
            super(null);
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

        @Override
        public Matrix getSubsample(double sample_probability) {
            Matrix newMatrix = mMatrix.sliceRow(0);
            for(int i = 1; i < mMatrix.getRows(); i++) {
                if (shouldKeep(sample_probability)) {
                    newMatrix.add(mMatrix.sliceRow(i));
                }
            }
            
            return newMatrix;
        }
}
