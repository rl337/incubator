package org.rl337.math;

public interface Distribution {
    
    /**
     * The area under the probability curve between 
     * negative infinity and some value x.
     * @param x upper limit of probability range
     * @return the cumulative probability of the distribution less than x
     */
    double pr(double x);
    
    /**
     * This calculates the point in the sample set x such that
     * pr(x) provides the value defined by prob.
     * 
     * @param percent a probability between 0 and 1
     * @return the value in the sample set that represents pr(x) = prob
     */
    double q(double prob);

}
