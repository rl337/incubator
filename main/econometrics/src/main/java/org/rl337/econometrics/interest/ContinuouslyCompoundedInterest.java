package org.rl337.econometrics.interest;

import org.rl337.econometrics.Period;

public class ContinuouslyCompoundedInterest extends AbstractInterest {
    
    public ContinuouslyCompoundedInterest(double principal, double rate) {
        super(principal, rate, Period.PERIOD_ANNUALLY);
    }
    public double value(Period periods) {
        return getPrincipal() * Math.pow(Math.E, getRate() * getRawPeriods(periods));
    }
    

    public double getEffectiveAnnualRate() {
        return Math.pow(Math.E, getRate()) - 1;
    }

}
