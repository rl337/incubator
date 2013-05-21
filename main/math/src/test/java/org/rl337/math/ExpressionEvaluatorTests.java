package org.rl337.math;

import java.util.HashMap;

import org.rl337.math.lang.Token;
import org.rl337.math.lang.TokenType;

import junit.framework.TestCase;

public class ExpressionEvaluatorTests extends TestCase {
    private HashMap<String, Token> mVariables;
    
    public void setUp() {
        mVariables = new HashMap<String, Token>();
        
        mVariables.put("pi", new Token(TokenType.Number, Double.toString(Math.PI), -1));
        mVariables.put("e", new Token(TokenType.Number, Double.toString(Math.E), -1));
        mVariables.put("blargle", new Token(TokenType.Number, Double.toString(1.5), -1));
        mVariables.put("sheet1!a243", new Token(TokenType.Number, Double.toString(8.24), -1));
    }

    public void assertExpression(String expr, String expected, String msg) throws Exception {
        assertEquals(msg, expected, ExpressionEvaluator.evaluate(expr, mVariables));
    }
    
    public void testParseConstantsAndIdentifiers() throws Exception {
        assertExpression("10", "10", "Constant should evaluate as the same");
        assertExpression("10.00", "10.00", "Constant should evaluate as the same");
        
        assertExpression("E", Double.toString(Math.E), "E should evaluate to Math.E in upper case");
        assertExpression("e", Double.toString(Math.E), "E should evaluate to Math.E in lower case");
        assertExpression("BlArGlE", "1.5", "blargle should evaluate even with mixed case");
        assertExpression("sheet1!A243", "8.24", "spreadsheet variable should be valid");
    }
    
    public void testParseSimpleExpressions() throws Exception {
        assertExpression("10+1", "11.0", "10 + 1 = 11");
        assertExpression(" 10 + 1 ", "11.0", "10 + 1 = 11");
        assertExpression("10 +1  ", "11.0", "10 + 1 = 11");
    }
    
    public void testParseIdentifierExpressions() throws Exception {
        assertExpression("pi + e", Double.toString(Math.E + Math.PI), "pi + e");
        assertExpression("blargle * 2", "3.0", "blargle * 2 = 3");
    }
    
    public void testParseExpressionWithParens() throws Exception {
        assertExpression("3 * (4 + 1)", "15.0", "3 * (4 + 1) = 15");
        assertExpression("(4 + 1) * 3", "15.0", "(4 + 1) * 3 = 15");
        assertExpression("((4 + 1) * 2)^2 ", "100.0", "((4 + 1) * 2)^2 = 100");
        assertExpression("((((4))))", "4", "((((4)))) = 4");
        assertExpression("pi * (4 ^ 2)", Double.toString(Math.PI * 16), "pi * (4 ^ 2)");
    }
    
    public void testOrderOfOperations() throws Exception {
        assertExpression("4 + 1 * 3", "7.0", "4 + 1 * 3 = 7");
        assertExpression("4 + 1 ^ 3", "5.0", "4 + 1 ^ 3 = 5");
    }
}
