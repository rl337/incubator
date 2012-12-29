package org.rl337.econometrics;

public class Asset {
    private double[] mPrice;
    private double[] mDividends;
    private double[] mCPI;
    private Period mPeriod;

    public Asset(double[] price, double[] dividends, double[] cpi, Period period) {
        mPrice = price;
        mDividends = dividends;
        mCPI = cpi;
        mPeriod = period;
    }
    
    public Asset(double[] price, double[] dividends, Period period) {
        this(price, dividends, null, period);
    }
    
    public Asset(double[] price, Period period) {
        this(price, null, null, period);
    }
    
    public double getGrossReturn(Period start, Period stop) {
        int startPeriod = (int) start.count(mPeriod);
        int stopPeriod = (int) stop.count(mPeriod);

        double grossReturn = 1;
        for(int lastPeriod = startPeriod; lastPeriod < stopPeriod; lastPeriod++) {
            int thisPeriod = lastPeriod + 1;

            double priceThisPeriod = mPrice[thisPeriod];
            if (mDividends != null) {
                priceThisPeriod += mDividends[thisPeriod];
            }
            
            
            double grossReturnThisPeriod = priceThisPeriod / mPrice[lastPeriod];
            
            if (mCPI != null) {
                double cpiThisPeriod = (mCPI[thisPeriod] - mCPI[lastPeriod]) / mCPI[lastPeriod] + 1;
                grossReturnThisPeriod = grossReturnThisPeriod / cpiThisPeriod;
            }
            
            grossReturn *= grossReturnThisPeriod;
        }
        
        return grossReturn;
    }
    
    public double getNetReturn(Period start, Period stop) {
        return getGrossReturn(start, stop) - 1;
    }
    
    public double price(Period p) {
        int period = (int) p.count(mPeriod);
        
        return mPrice[period];
    }
    
    public double getAnnualizedGrossRate(Period start, Period stop) {
        Period effectivePeriod = stop.delta(start);
        
        double compoundings = Period.PERIOD_ANNUALLY.count(effectivePeriod);
        double grossReturn = getGrossReturn(start, stop);
        
        return Math.pow(grossReturn, compoundings);
    }
    
    public double getContinuallyCompoundedNetRate(Period start, Period stop) {
        return Math.log(getGrossReturn(start, stop));
    }
    
    public double getContinuouslyCompoundedAnnualizedGrossRate(Period start, Period stop) {
        
//        Period effectivePeriod = stop.delta(start);
//        
//        double compoundings = Period.PERIOD_ANNUALLY.count(effectivePeriod);
//        double netRate = getContinuallyCompoundedNetRate(start, stop);
//        double grossReturn = 1 + netRate;
//        
//        return Math.pow(grossReturn, compoundings);
        double grossReturn = getAnnualizedGrossRate(start, stop);
        return Math.log(grossReturn);
    }
}
