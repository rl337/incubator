package org.rl337.math.lang;

public class ExpressionEvaluationException extends Exception {
    private static final long serialVersionUID = 861296746092944093L;
    
    public ExpressionEvaluationException(String str, int pos) {
        this(str + " (pos: " + pos + ")");
    }
    
    public ExpressionEvaluationException(String str) {
        super(str);
    }
}
