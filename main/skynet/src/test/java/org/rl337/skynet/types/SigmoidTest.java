package org.rl337.skynet.types;

import org.rl337.math.types.Matrix;

import junit.framework.TestCase;

public class SigmoidTest extends TestCase {
    public void assertValue(double x, double y) {
        Matrix m = Matrix.matrix(1, 1, x);
        Matrix n = Sigmoid.RealSigmoid.evaluate(m);
        
        assertEquals(y, n.getValue(0,0), 1.0E-10d);
    }
    
    public void testCriticalValues() {
        assertValue(0, 0.5);
        assertValue(Double.POSITIVE_INFINITY, 1.0);
        assertValue(Double.NEGATIVE_INFINITY, 0.0);
    }
}
