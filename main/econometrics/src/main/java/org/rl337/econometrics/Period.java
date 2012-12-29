package org.rl337.econometrics;

public class Period {
    private static final double smAnnualValue = 1.0;
    
    public static Period PERIOD_ANNUALLY = new Period(smAnnualValue);
    public static Period PERIOD_SEMIANNUALLY =  new Period(smAnnualValue / 2);
    public static Period PERIOD_QUARTERLY =  new Period(smAnnualValue / 4);
    public static Period PERIOD_WEEKLY =  new Period(smAnnualValue / 52.1775);
    public static Period PERIOD_MONTHLY =  new Period(smAnnualValue / 12);
    public static Period PERIOD_DAILY =  new Period(smAnnualValue / 365.242);
    
    private double mValue;
    
    private Period(double value) {
        mValue = value;
    }
    
    public double count(Period t) {
        if (t.mValue == 0) {
            return 0;
        }
        
        if (mValue == 0) {
            return 0;
        }
 
        return mValue / t.mValue;
    }
    
    public Period divide(double parts) {
        return new Period(mValue / parts);
    }
    
    public Period multiple(double scale) {
        return new Period(mValue * scale);
    }
    
    public Period delta(Period p) {
        return new Period(mValue - p.mValue);
    }
}
