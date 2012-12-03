package org.rl337.skynet.types;

import junit.framework.TestCase;

public class LogTest extends TestCase {

    public void assertValue(double x, double y) {
        Matrix m = Matrix.matrix(1, 1, x);
        Matrix n = Log.RealLog.evaluate(m);
        
        assertEquals(y, n.getValue(0,0), 1.0E-10d);
    }
    
    public void testCriticalValues() {
        assertValue(Math.pow(Math.E, 10.0), 10);
        assertValue(10.0, 2.30258509299405);
        assertValue(1.0, 0.0);
        assertValue(0.5, -0.693147180559945);
        assertValue(0.25, -1.38629436111989);
        assertValue(0.125, -2.07944154167984);
    }

}
