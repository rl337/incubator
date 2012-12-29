package org.rl337.econometrics;

import junit.framework.TestCase;

public class AssetTest extends TestCase {

    public void testSinglePeriodReturn() {
        double values[] = { 85, 90 };
        Asset msft = new Asset(values, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross return should be 1.0588", 1.0588, msft.getGrossReturn(start, end), 0.0001);
        assertEquals("net return should be 0.0588", 0.0588, msft.getNetReturn(start, end), 0.0001);
    }
    
    public void testMultiplePeriodReturn() {
        double values[] = { 80, 85, 90 };
        Asset msft = new Asset(values, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(2);
        
        assertEquals("gross return should be 1.125", 1.125, msft.getGrossReturn(start, end));
        assertEquals("net return should be 0.125", 0.125, msft.getNetReturn(start, end));
    }
    
    public void testDividend() {
        double values[] = { 85, 90 };
        double dividends[] = { 0, 1 };
        Asset msft = new Asset(values, dividends, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross return should be 1.0706", 1.0706, msft.getGrossReturn(start, end), 0.0001);
        assertEquals("net return should be 0.0706", 0.0706, msft.getNetReturn(start, end), 0.0001);
    }
    
    
    public void testInflationAdjustment() {
        double values[] = { 85, 90 };
        double dividends[] = { 0, 0 };
        double cpi[] = { 1, 1.01 };
        Asset msft = new Asset(values, dividends, cpi, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross return should be 1.0483", 1.0483, msft.getGrossReturn(start, end), 0.0001);
        assertEquals("net return should be 0.0483", 0.0483, msft.getNetReturn(start, end), 0.0001);
    }
    
    public void testAnnualizedGrossReturn() {
        double values[] = { 85, 90 };
        Asset msft = new Asset(values, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross annualized return should be 1.9856", 1.9856, msft.getAnnualizedGrossRate(start, end), 0.0001);
    }
    
    public void testAnnualizedGrossReturn2() {
        double values[] = { 50, 90 };
        Period twoYears = Period.PERIOD_ANNUALLY.multiple(2);
        Asset msft = new Asset(values, twoYears);
        
        Period start = twoYears.multiple(0);
        Period end = twoYears.multiple(1);
        
        assertEquals("gross annualized return should be 1.3416", 1.3416, msft.getAnnualizedGrossRate(start, end), 0.0001);
    }
    
    public void testContinuallyCompoundedReturn() {
        double values[] = { 85, 90 };
        Asset msft = new Asset(values, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross return should be 0.0571", 0.0571, msft.getContinuallyCompoundedNetRate(start, end), 0.0001);
    }
    
    public void testRealContinuallyCompoundedReturn() {
        double values[] = { 85, 90 };
        double dividends[] = { 0.0, 0.0 };
        double cpi[] = { 1.0, 1.01 };
        Asset msft = new Asset(values, dividends, cpi, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(1);
        
        assertEquals("gross return should be 0.047", 0.047, msft.getContinuallyCompoundedNetRate(start, end), 0.001);
    }
    
    public void testMultiplePeriodContinuallyCompoundedReturn() {
        double values[] = { 80, 85, 90 };
        Asset msft = new Asset(values, Period.PERIOD_MONTHLY);
        
        Period start = Period.PERIOD_MONTHLY.multiple(0);
        Period end = Period.PERIOD_MONTHLY.multiple(2);
        
        assertEquals("gross return should be 0.1178", 0.1178, msft.getContinuallyCompoundedNetRate(start, end), 0.0001);
    }
}
