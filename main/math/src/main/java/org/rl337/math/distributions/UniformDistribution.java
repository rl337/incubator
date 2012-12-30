package org.rl337.math.distributions;

import org.rl337.math.Distribution;

/**
 * This class defines a uniform distribution over some sample space
 * with well defined upper and lower bounds.
 * 
 * 
 * @author Richard Lee
 *
 */
public class UniformDistribution implements Distribution {
    private double mR1;
    private double mR2;
    private double mDelta;
    
    /**
     * Create a uniform distribution whose sample space is strictly
     * defined by the lower bound r1 and upper bound r2.  Note that
     * r2 MUST be larger than r1 for this distribution to have meaning.
     * @param r1 lower bound of unified distribution
     * @param r2 upper bound of unified distribution
     */
    public UniformDistribution(double r1, double r2) {
        if (r2 < r1) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound");
        }
        
        mR1 = r1;
        mR2 = r2;
        mDelta = r2 - r1;
    }

    /**
     * @see Distribution#pr(double)
     */
    public double pr(double x) {
        
        // If x falls below the lower bound of the sample space,
        // the probability must be 0.
        if (x < mR1) {
            return 0.0;
        }
        
        // If x falls above the upper bound of the sample space
        // the probability will be 1.0
        if (x > mR2) {
            return 1.0;
        }
        
        return (x - mR1) / mDelta;
    }
    
    /**
     * @see Distribution#q(double)
     */
    public double q(double prob) {
        return prob * mDelta + mR1;
    }

}
