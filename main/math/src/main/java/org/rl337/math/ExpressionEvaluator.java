package org.rl337.math;

import java.util.Map;

import org.rl337.math.lang.ExpressionEvaluationException;
import org.rl337.math.lang.MapVariableResolver;
import org.rl337.math.lang.Parser;
import org.rl337.math.lang.Token;
import org.rl337.math.lang.VariableResolver;


public class ExpressionEvaluator {

    public static String evaluate(String expr, VariableResolver resolver) throws ExpressionEvaluationException {
        Parser p = new Parser(resolver);
        
        return p.evaluate(expr);
    }
    
    public static String evaluate(String expr) throws ExpressionEvaluationException {
        return evaluate(expr, (VariableResolver) null);
    }
    
    public static String evaluate(String expr, Map<String, Token> variables) throws ExpressionEvaluationException {
        return evaluate(expr, new MapVariableResolver(variables));
    }

}
