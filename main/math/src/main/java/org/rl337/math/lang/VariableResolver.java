package org.rl337.math.lang;


public interface VariableResolver {
    Token resolve(String name, int pos) throws ExpressionEvaluationException;
}
