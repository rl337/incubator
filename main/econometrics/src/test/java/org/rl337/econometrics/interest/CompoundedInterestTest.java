package org.rl337.econometrics.interest;

import org.rl337.econometrics.Interest;
import org.rl337.econometrics.Period;

import junit.framework.TestCase;

public class CompoundedInterestTest extends TestCase {

    public void testInterestRate() {
        double principal = 1000;
        double interest = 0.1;
        
        assertInterestRate(principal, interest, Period.PERIOD_ANNUALLY, 1100.0, 0.10);
        assertInterestRate(principal, interest, Period.PERIOD_QUARTERLY, 1103.8128906249995, 0.10381289062499954);
        assertInterestRate(principal, interest, Period.PERIOD_WEEKLY, 1105.0651533242549, 0.10506515332425481);
        assertInterestRate(principal, interest, Period.PERIOD_DAILY, 1105.1557916434274, 0.10515579164342737);
    }
    
    public void assertInterestRate(double principal, double rate, Period timePeriod, double expectedValue, double expectedEffectiveRate) {
        Interest i = new CompoundedInterest(principal, rate, timePeriod);
        
        assertEquals("Total was not expected value", expectedValue, i.value(Period.PERIOD_ANNUALLY), 0.000001);
        assertEquals("Effective rate was not expected", expectedEffectiveRate, i.getEffectiveAnnualRate(), 0.000001);
    }
    
    
}
