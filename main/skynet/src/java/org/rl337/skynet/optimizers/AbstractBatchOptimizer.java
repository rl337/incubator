package org.rl337.skynet.optimizers;

import org.rl337.skynet.CostFunction;
import org.rl337.skynet.DataSet;
import org.rl337.skynet.Hypothesis;
import org.rl337.skynet.Optimizer;
import org.rl337.skynet.types.Matrix;

public abstract class AbstractBatchOptimizer extends Optimizer {
    private int mBatchSize;

    public AbstractBatchOptimizer(Hypothesis h, CostFunction c, int batchSize) {
        super(h, c);
        mBatchSize = batchSize;
    }
    
    public int getBatchSize() {
        return mBatchSize;
    }
    
    public void setBatchSize(int size) {
        mBatchSize = size;
    }

    @Override
    public Matrix run(Matrix theta, DataSet training, DataSet labels) {
        Matrix x = training.getNextBatch(mBatchSize);
        Matrix y = labels.getNextBatch(mBatchSize);
        while (x != null && y != null) {
            theta = runBatch(theta, x, y);
            
            x = training.getNextBatch(mBatchSize);
            y = labels.getNextBatch(mBatchSize);
        }
        
        return theta;
    }

    protected abstract Matrix runBatch(Matrix theta, Matrix training, Matrix labels);

}
