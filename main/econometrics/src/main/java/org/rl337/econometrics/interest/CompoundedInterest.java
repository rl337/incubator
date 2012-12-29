package org.rl337.econometrics.interest;

import org.rl337.econometrics.Period;

public class CompoundedInterest extends AbstractInterest {
    
    public CompoundedInterest(double principal, double interest, Period compoundingPeriod) {
        super(principal, interest, compoundingPeriod);
    }
    
    public double value(Period time) {
        double basePeriod = getPeriod().count(Period.PERIOD_ANNUALLY);
        return getPrincipal() * Math.pow(1 + getRate() / basePeriod, getRawPeriods(time));
    }

    public double getEffectiveAnnualRate() {
        double basePeriod = getPeriod().count(Period.PERIOD_ANNUALLY);
        return Math.pow(1 + getRate() / basePeriod, basePeriod) - 1;
    }

}
