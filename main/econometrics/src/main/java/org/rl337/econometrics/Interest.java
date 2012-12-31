package org.rl337.econometrics;

public interface Interest {
    public double value(Period periods);
    public double getEffectiveAnnualRate();

}
