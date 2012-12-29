package org.rl337.econometrics;

import junit.framework.TestCase;

public class PortfolioTest extends TestCase {
    
    public void testGrossReturn() {
        Period purchased = Period.PERIOD_MONTHLY.multiple(0);
        Period start = Period.PERIOD_MONTHLY.multiple(1);
        Period end = Period.PERIOD_MONTHLY.multiple(2);
        
        Asset msft = new Asset(new double[] {80, 85, 90}, Period.PERIOD_MONTHLY);
        Asset sbux = new Asset(new double[] {30, 30, 28}, Period.PERIOD_MONTHLY);
        
        Portfolio p = new Portfolio(
            new Asset[] {msft, sbux},
            new double[] {10, 10},
            purchased
        );
        
        assertEquals("Rate of return should have been 1,127.05", 1127.05, p.getGrossReturn(start, end), 0.01);
    }
    
    public void testNetReturn() {
        Period purchased = Period.PERIOD_MONTHLY.multiple(0);
        Period start = Period.PERIOD_MONTHLY.multiple(1);
        Period end = Period.PERIOD_MONTHLY.multiple(2);
        
        Asset msft = new Asset(new double[] {80, 85, 90}, Period.PERIOD_MONTHLY);
        Asset sbux = new Asset(new double[] {30, 30, 28}, Period.PERIOD_MONTHLY);
        
        Portfolio p = new Portfolio(
            new Asset[] {msft, sbux},
            new double[] {10, 10},
            purchased
        );
        
        assertEquals("Rate of return should have been 27.05", 27.05, p.getNetReturn(start, end), 0.01);
    }
    
    public void testContinuouslyCompoundedReturnRate() {
        Period start = Period.PERIOD_MONTHLY.multiple(1);
        Period end = Period.PERIOD_MONTHLY.multiple(2);
        
        Asset msft = new Asset(new double[] {80, 85, 90}, Period.PERIOD_MONTHLY);
        Asset sbux = new Asset(new double[] {30, 30, 28.49}, Period.PERIOD_MONTHLY);
        
        assertEquals("sbux rate should be -0.0503", -0.0503, sbux.getNetReturn(start, end), 0.0001);
        
        Portfolio p = new Portfolio(
            new Asset[] {msft, sbux},
            1100,
            new double[] {0.25, 0.75}
        );
        
        assertEquals("continuously compounded net rate of return", -0.02329, p.getContinuallyCompoundedNetRate(start, end), 0.01);
    }

}
