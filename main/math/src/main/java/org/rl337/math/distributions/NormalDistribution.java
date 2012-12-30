package org.rl337.math.distributions;

import org.rl337.math.Distribution;

public class NormalDistribution implements Distribution {
    private static final double smSqrt2 = Math.sqrt(2);
    
    public static final NormalDistribution STANDARD = new NormalDistribution(0.0, 1.0);
    
    private double mMu;
    private double mSigma;
    
    /**
     * Create a Normal Distribution with mean mu and standard deviation sigma.
     * @param mu the mean of the distribution
     * @param sigma the standard deviation of the distribution
     */
    public NormalDistribution(double mu, double sigma) {
        mMu = mu;
        mSigma = sigma;
    }
    
    public double pr(double x) {
        if ((x - mMu) > 0) {
            return (1 + erf((x - mMu) / (mSigma * smSqrt2))) / 2;
        }
        
        // Approximation is only designed for positive x. 
        // for negative x, we subtract erf(x) for abs(x)
        return (1 - erf((-x + mMu) / (mSigma * smSqrt2))) / 2;
    }

    public double q(double prob) {
        return smSqrt2 * erfinv(2 * prob - 1);
    }
    
    private static final double p = 0.3275911;
    private static final double a1 = 0.254829592;
    private static final double a2 = -0.284496736;
    private static final double a3 = 1.421413741;
    private static final double a4 = -1.453152027;
    private static final double a5 = 1.061405429;
    
    /**
     * This method approximates the error function (erf).
     * This code was adopted from Abramowitz and Stegun approximation described
     * on the Error Function wikipedia page.  It claims a maximum
     * error of 1.5e-7
     * 
     * @param x the value to find erf(x) of
     * @return returns an approximation of erf(x)
     * @see http://en.wikipedia.org/wiki/Error_function
     */
    private static double erf(double x) {
        double t = 1.0 / (1 + p * x);
        
        double t2 = t * t;
        double t3 = t2 * t;
        double t4 = t2 * t2;
        double t5 = t4 * t;
        
        double sum = a1 * t + a2 * t2 + a3 * t3 + a4 * t4 + a5 * t5;
        return 1 - sum * Math.pow(Math.E, -x * x);
    }
    
    /*
     * This method approximates the inverse error function using the 
     * associated Mclauren series out to the 6th term. See wikipedia
     * page on the Error function.
     * @param z error value whose param we want to find
     * @return the inverse error function for error z

    private static double c1 = 0.26179938779914935;
    private static double c2 = 0.14393173084921976;
    private static double c3 = 0.09766361950392052;
    private static double c4 = 0.07329907936638083;
    private static double c5 = 0.05837250087858446;
    private static double c6 = 0.04833606317017819;
    private static double c7 = 0.041147394940524704;
    private static double c8 = 0.03575721309236461;
    private static double c9 = 0.0315727633198465;

    private static double smSqrtPi = Math.sqrt(Math.PI);

    private static double erfinv2(double z) {
        
        double z2 = z * z;
        double z3 = z2 * z;
        double z5 = z3 * z2;
        double z7 = z5 * z2;
        double z9 = z7 * z2;
        double z11 = z9 * z2;
        double z13 = z11 * z2;
        double z15 = z13 * z2;
        double z17 = z15 * z2;
        double z19 = z17 * z2;
        
        double sum = z + z3*c1 + z5*c2 + z7*c3 + z9*c4 + z11*c5 + z13*c6 + z15*c7 + z17*c8 + z19*c9;
        
        return 0.5 * smSqrtPi * sum;
    }
    
         */

    //private static final double a = (8 * (Math.PI - 3)) / (3 * Math.PI * (4 - Math.PI));
    private static final double a = 0.147;
    private static double erfinv(double z) {
        double v = 2.0 / (Math.PI * a);
        double w = Math.log(1 - z * z);
        double x = v + w / 2.0;
        double y = Math.sqrt(x * x - w / a);
        return Math.signum(z) * Math.sqrt(y - x);
    }


}
