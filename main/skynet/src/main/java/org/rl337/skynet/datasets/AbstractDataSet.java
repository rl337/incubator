package org.rl337.skynet.datasets;

import java.util.Random;

import org.rl337.skynet.DataSet;

public abstract class AbstractDataSet implements DataSet {
    private Random mRandom;

    protected AbstractDataSet(Random random) {
        if (random == null) {
            random = new Random();
        }
        
        mRandom = random;
    }

    protected boolean shouldKeep(double prob) {
        return mRandom.nextDouble() < prob;
    }

}