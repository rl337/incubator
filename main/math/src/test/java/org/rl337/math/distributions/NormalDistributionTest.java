package org.rl337.math.distributions;

import org.rl337.math.Distribution;

import junit.framework.TestCase;

public class NormalDistributionTest extends TestCase {
    private static final double smPrMaxDoubleDelta = 0.000001;
    private static final double smQMaxDoubleDelta = 0.0075;
    
    public void testStandardNormalDistribution() {
        Distribution d = NormalDistribution.STANDARD;
        
        assertEquals("p(0,0,1)", 0.5, d.pr(0.0), smPrMaxDoubleDelta);
        assertEquals("p(1,0,1)", 0.8413447, d.pr(1.0), smPrMaxDoubleDelta);
        assertEquals("p(2,0,1)", 0.9772499, d.pr(2.0), smPrMaxDoubleDelta);
        assertEquals("p(4,0,1)", 0.9999683, d.pr(4.0), smPrMaxDoubleDelta);
        
        assertEquals("p(-1,0,1)", 1 - 0.8413447, d.pr(-1.0), smPrMaxDoubleDelta);
        assertEquals("p(-2,0,1)", 1 - 0.9772499, d.pr(-2.0), smPrMaxDoubleDelta);
        assertEquals("p(-4,0,1)", 1 - 0.9999683, d.pr(-4.0), smPrMaxDoubleDelta);
        
        assertEquals("q(0.5,0,1)", 0.0, d.q(0.5), smQMaxDoubleDelta);
        assertEquals("q(0.8413447,0,1)", 1, d.q(0.8413447), smQMaxDoubleDelta);
        assertEquals("q(0.9772499,0,1)", 2, d.q(0.9772499), smQMaxDoubleDelta);
        assertEquals("q(0.9999683,0,1)", 4, d.q(0.9999683), smQMaxDoubleDelta);
        
        assertEquals("q(0.1586553,0,1)", -1, d.q(0.1586553), smQMaxDoubleDelta);
        assertEquals("q(0.0227501,0,1)", -2, d.q(0.0227501), smQMaxDoubleDelta);
        assertEquals("q(3.17e-05,0,1)", -4, d.q(3.17e-05), smQMaxDoubleDelta);
    }
    
    public void testNormalDistribution() {
        Distribution d = new NormalDistribution(0.5, 0.5);
        
        assertEquals("pr(0, 0.5, 0.5)", 0.5, d.pr(0.5), smPrMaxDoubleDelta);
        
        assertEquals("pr(0, 0.5, 0.5)", 0.1586553, d.pr(0.0), smPrMaxDoubleDelta);
        assertEquals("pr(1, 0.5, 0.5)", 0.8413447, d.pr(1.0), smPrMaxDoubleDelta);
        assertEquals("pr(2, 0.5, 0.5)", 0.9986501, d.pr(2.0), smPrMaxDoubleDelta);
        assertEquals("pr(4, 0.5, 0.5)", 1.0, d.pr(4.0), smPrMaxDoubleDelta);
        
        assertEquals("pr(-1, 0.5, 0.5)", 0.001349898, d.pr(-1.0), smPrMaxDoubleDelta);
        assertEquals("pr(-2, 0.5, 0.5)", 2.866516e-07, d.pr(-2.0), smPrMaxDoubleDelta);
        assertEquals("pr(-4, 0.5, 0.5)", 0.0, d.pr(-4.0), smPrMaxDoubleDelta);
        
        assertEquals("q(0.1586553, 0.5, 0.5)", 0, d.q(0.1586553), smQMaxDoubleDelta);
        assertEquals("q(0.8413447, 0.5, 0.5)", 1, d.q(0.8413447), smQMaxDoubleDelta);
        assertEquals("q(0.9986501, 0.5, 0.5)", 2, d.q(0.9986501), smQMaxDoubleDelta);
        
        assertEquals("q(0.001349898, 0.5, 0.5)", -1, d.q(0.001349898), smQMaxDoubleDelta);
        assertEquals("q(2.866516e-07, 0.5, 0.5)", -2, d.q(2.866516e-07), smQMaxDoubleDelta);
    }

}
