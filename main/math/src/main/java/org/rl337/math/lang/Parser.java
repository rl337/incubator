package org.rl337.math.lang;


public class Parser {
    private VariableResolver mResolver;
     
    public Parser(VariableResolver resolver) {
        mResolver = resolver;
    }
    
    public String evaluate(String expr) throws ExpressionEvaluationException {
        Lexer lex = new Lexer(expr);
        TokenStack leftStack = new TokenStack();
        TokenStack rightStack = lex.evaluate();
        
        while(!rightStack.isEmpty()) {
            Token t = rightStack.shift();
            //printStacks(leftStack, t, rightStack);
            
            switch(t.getType()) {
                case Number: evaluateNumber(leftStack, rightStack, t); break;
                case Identifier: evaluateIdentifier(leftStack, rightStack, t); break;
                case Plus: evaluatePlus(leftStack, rightStack, t); break;
                case Minus: evaluateMinus(leftStack, rightStack, t); break;
                case Star: evaluateStar(leftStack, rightStack, t); break;
                case Slash: evaluateSlash(leftStack, rightStack, t); break;
                case Caret: evaluateCaret(leftStack, rightStack, t); break;
                case Percent: evaluatePercent(leftStack, rightStack, t); break;
                case OpenParen: evaluateOpenParen(leftStack, rightStack, t); break;
                case CloseParen: evaluateCloseParen(leftStack, rightStack, t); break;
            }
        }
        
        if (!rightStack.isEmpty()) {
            throw new ExpressionEvaluationException("Syntax error", lex.getPosition());
        }
        
        if (leftStack.size() > 1) {
            throw new ExpressionEvaluationException("Parse error", lex.getPosition());
        }
        
        Token finalToken = leftStack.pop();
        if (finalToken.getType() != TokenType.Number) {
            throw new ExpressionEvaluationException("Parse error", lex.getPosition());
        }
        
        return finalToken.getValue();
    }
    
    private void evaluateOpenParen(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        l.push(t);
    }
    
    private void evaluateCloseParen(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        Token prevToken = l.pop();
        if (prevToken == null) {
            throw new ExpressionEvaluationException("Unexpected " + TokenType.CloseParen);
        }
        
        Token openParen = l.pop();
        if (openParen == null) {
            throw new ExpressionEvaluationException("Found " + TokenType.CloseParen + " with no " + TokenType.OpenParen);
        }
        
        r.unshift(prevToken);
        Token beforeOpenParen = l.pop();
        if (beforeOpenParen != null) {
            r.unshift(beforeOpenParen);
        }
    }
    
    private void evaluateNumber(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        if (l.isEmpty() || l.peekType() == TokenType.OpenParen) {
            l.push(t);
            return;
        }
        
        // Here we have a number on the right side of a bunch of stuff... back up.
        r.unshift(t);
        r.unshift(l.pop());
    }
    
    private void evaluateIdentifier(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        if (mResolver == null) {
            throw new ExpressionEvaluationException("No Resolver supplied. Cannot resolve identifiers");
        }
        
        // We need to check to see if this is a function. If it is, evaluate the function.
        
        // This doesn't seem to be a function call. Assume that it's a variable to resolve.
        Token val = mResolver.resolve(t.getValue(), t.getPos());
        r.unshift(val);
    }
    
    private void evaluatePlus(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Plus, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return lvalue + rvalue;
            }
        });
    }
    
    private void evaluateMinus(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Minus, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return lvalue - rvalue;
            }
        });
    }
    
    private void evaluateStar(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Star, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return lvalue * rvalue;
            }
        });
    }
    
    private void evaluateSlash(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Slash, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return lvalue / rvalue;
            }
        });
    }
    
    private void evaluateCaret(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Caret, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return Math.pow(lvalue, rvalue);
            }
        });
    }
    
    private void evaluatePercent(TokenStack l, TokenStack r, Token t) throws ExpressionEvaluationException {
        evaluateBinaryOperator(TokenType.Caret, l, r, t, new BinaryOperation() {
            public double evaluate(double lvalue, double rvalue) {
                return lvalue % rvalue;
            }
        });
    }
    
    private void evaluateBinaryOperator(TokenType type, TokenStack l, TokenStack r, Token t, BinaryOperation op) throws ExpressionEvaluationException {
        if (mResolver == null) {
            throw new ExpressionEvaluationException("No Resolver supplied. Cannot resolve identifiers");
        }
        
        Token rvalue = r.shift();
        if (rvalue == null) {
            throw new ExpressionEvaluationException(type.toString() + " needs an rvalue", t.getPos());
        }
        
        // There's something with more order of operations than this..
        // we need to do that first, so push everything left.
        Token lookAheadToken = r.peekBottom();
        if (lookAheadToken != null) {
            if (lookAheadToken.getType().getPriority() > type.getPriority()) {
                l.push(t);
                l.push(rvalue);
                return;
            }
        }

        Token lvalue = l.pop();
        if (lvalue == null) {
            throw new ExpressionEvaluationException(type.toString() + " needs an lvalue", t.getPos());
        }
        
        // If we've got an open paren, put everything on the left side so we can deal with the paren.
        if (rvalue.getType() == TokenType.OpenParen) {
            l.push(lvalue);
            l.push(t);
            r.unshift(rvalue);
            return;
        }
        
        if (rvalue.getType() == TokenType.Identifier) {
            rvalue = mResolver.resolve(rvalue.getValue(), rvalue.getPos());
        }
        
        if (rvalue.getType() != TokenType.Number) {
            throw new ExpressionEvaluationException("Expected number but found " + rvalue.getType().toString(), t.getPos());
        }
        
        double ldoubleval = Double.parseDouble(lvalue.getValue());
        double rdoubleval = Double.parseDouble(rvalue.getValue());
        
        double result = op.evaluate(ldoubleval, rdoubleval);
        
        r.unshift(new Token(TokenType.Number, Double.toString(result), t.getPos()));
    }
    
    protected void printStacks(TokenStack l, Token t, TokenStack r) {
        for (int i = 0; i < l.size(); i++) {
            System.out.print(l.get(i));
            System.out.print(" ");
        }
        System.out.print(" [ " + t + "] ");
        
        for (int i = 0; i < r.size(); i++) {
            System.out.print(r.get(i));
            System.out.print(" ");
        }
        System.out.println();
    }

    
    private static interface BinaryOperation {
        double evaluate(double lvalue, double rvalue);
    }
}
