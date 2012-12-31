package org.rl337.econometrics;

public class Rounder {
    public static double round(double value, int digits) {
        double scale = Math.pow(10, digits);
        
        return Math.round(value * scale) / scale;
    }
}
