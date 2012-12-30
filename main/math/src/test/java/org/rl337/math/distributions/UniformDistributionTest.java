package org.rl337.math.distributions;

import org.rl337.math.Distribution;

import junit.framework.TestCase;

public class UniformDistributionTest extends TestCase {
    public void testSimpleUnifiedDistribution() {
        Distribution d = new UniformDistribution(0.0, 100.0);
        
        assertEquals("Value below lower bound should be 0", 0.0, d.pr(-5), 0.000001);
        assertEquals("Value above upper bound should be 1", 1.0, d.pr(1000), 0.000001);
        
        assertEquals("pr quarter", 0.25, d.pr(25), 0.000001);
        assertEquals("pr third", 0.3333333333, d.pr(100.0 / 3), 0.000001);
        assertEquals("pr half", 0.5, d.pr(50), 0.000001);
        assertEquals("pr one", 1.0, d.pr(100), 0.000001);
        
        assertEquals("Quantile of 0 should be 0", 0.0, d.q(0), 0.000001);
        
        assertEquals("q quarter", 25, d.q(0.25), 0.000001);
        assertEquals("q third", 100.0 / 3, d.q(1.0 / 3), 0.000001);
        assertEquals("q half", 50, d.q(0.5), 0.000001);
        assertEquals("q one", 100, d.q(1.0), 0.000001);
    }
    
    public void testUnifiedDistribution() {
        Distribution d = new UniformDistribution(-50.0, 50.0);
        
        assertEquals("Value below lower bound should be 0", 0.0, d.pr(-100), 0.000001);
        assertEquals("Value above upper bound should be 1", 1.0, d.pr(100), 0.000001);
        
        assertEquals("pr quarter", 0.25, d.pr(25 - 50), 0.000001);
        assertEquals("pr third", 0.3333333333, d.pr(100.0 / 3 - 50), 0.000001);
        assertEquals("pr half", 0.5, d.pr(50 - 50), 0.000001);
        assertEquals("pr one", 1.0, d.pr(100 - 50), 0.000001);
        
        assertEquals("q quarter", 25 - 50, d.q(0.25), 0.000001);
        assertEquals("q third", 100.0 / 3 - 50, d.q(1.0 / 3), 0.000001);
        assertEquals("q half", 50 - 50, d.q(0.5), 0.000001);
        assertEquals("q one", 100 - 50, d.q(1.0), 0.000001);
    }
}
