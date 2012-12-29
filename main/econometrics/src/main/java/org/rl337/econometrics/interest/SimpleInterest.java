package org.rl337.econometrics.interest;

import org.rl337.econometrics.Period;

public class SimpleInterest extends AbstractInterest {
    private double mPrincipal;
    private double mInterestRate;
    
    public SimpleInterest(double principal, double interest) {
        super(principal, interest, Period.PERIOD_ANNUALLY);
    }
    
    public double value(Period periods) {
        return mPrincipal + mPrincipal * mInterestRate * getRawPeriods(periods);
    }

    public double getEffectiveAnnualRate() {
        return mInterestRate;
    }

}
