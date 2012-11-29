package org.rl337.skynet;

import org.rl337.skynet.types.Matrix;

public interface DataSet {

    Matrix getNextBatch(int size);
    Matrix getAll();
    boolean hasMore();


}
