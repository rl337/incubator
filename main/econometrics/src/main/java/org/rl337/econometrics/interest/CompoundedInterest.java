package org.rl337.econometrics.interest;

import org.rl337.econometrics.Period;

public class CompoundedInterest extends AbstractInterest {
    
    public CompoundedInterest(double principal, double interest, Period compoundingPeriod) {
        super(principal, interest, compoundingPeriod);
    }
    
    public double value(Period time) {
        double basePeriod = Period.PERIOD_ANNUALLY.count(getPeriod());
        return getPrincipal() * Math.pow(1 + getRate() / basePeriod, getRawPeriods(time));
    }

    public double getEffectiveAnnualRate() {
        double basePeriod = Period.PERIOD_ANNUALLY.count(getPeriod());
        return Math.pow(1 + getRate() / basePeriod, basePeriod) - 1;
    }

}
