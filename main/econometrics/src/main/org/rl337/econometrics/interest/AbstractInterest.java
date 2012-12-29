package org.rl337.econometrics.interest;

import org.rl337.econometrics.Interest;
import org.rl337.econometrics.Period;

public abstract class AbstractInterest implements Interest {
    private double mPrincipal;
    private double mInterestRate;
    private Period mPeriod;
    
    public AbstractInterest(double principal, double rate, Period effectivePeriod) {
        mPrincipal = principal;
        mInterestRate = rate;
        mPeriod = effectivePeriod;
    }
    
    protected Period getPeriod() {
        return mPeriod;
    }
    
    protected double getRate() {
        return mInterestRate;
    }
    
    protected double getPrincipal() {
        return mPrincipal;
    }
    
    protected double getRawPeriods(Period time) {
        return mPeriod.count(time);
    }
}
