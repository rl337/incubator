package org.rl337.skynet;

import org.rl337.math.types.Matrix;

public interface DataSet {

    Matrix getNextBatch(int size);
    Matrix getAll();
    Matrix getSubsample(double sample_probability);
    boolean hasMore();


}
