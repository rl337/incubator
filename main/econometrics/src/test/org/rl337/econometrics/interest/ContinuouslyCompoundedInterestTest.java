package org.rl337.econometrics.interest;

import junit.framework.TestCase;

import org.rl337.econometrics.Interest;
import org.rl337.econometrics.Period;

public class ContinuouslyCompoundedInterestTest extends TestCase {

    public void testInterestRate() {
        double principal = 1000;
        double interest = 0.1;
        
        assertInterestRate(principal, interest, Period.PERIOD_ANNUALLY, 1105.1709180756477, 0.10517091807564771);
    }
    
    public void assertInterestRate(double principal, double rate, Period timePeriod, double expectedValue, double expectedEffectiveRate) {
        Interest i = new ContinuouslyCompoundedInterest(principal, rate);
        
        assertEquals("Total was not expected value", expectedValue, i.value(timePeriod), 0.000001);
        assertEquals("Effective rate was not expected", expectedEffectiveRate, i.getEffectiveAnnualRate(), 0.000001);
    }
    
}
