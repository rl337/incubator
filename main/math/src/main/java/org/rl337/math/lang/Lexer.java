package org.rl337.math.lang;


public class Lexer {
    private String mExpression;
    private int mPos;

    public Lexer(String expr) {
        mExpression = expr;
        mPos = 0;
    }
    
    public TokenStack evaluate() throws ExpressionEvaluationException {
        TokenStack result = new TokenStack();
        
        for(Token t = nextToken(); t != null; t = nextToken()) {
            result.push(t);
        }
        
        return result;
    }
    
    private Token nextToken() throws ExpressionEvaluationException {
        
        if (mPos >= mExpression.length()) {
            return null;
        }
        
        // skip any leading whitespace on the token
        getWhitespace();
        
        if (mPos >= mExpression.length()) {
            return null;
        }
        
        // first see if it's a number
         Token result = getNumber();
        if (result != null) {
            return result;
        }
        
        // next, see if it's a variable
        result = getIdentifier();
        if (result != null) {
            return result;
        }
        
        // finally return if it's an operator... if not, throw
        result = getOperator();
        if (result == null) {
            throw new ExpressionEvaluationException("Unknown symbol", mPos);
        }
        
        return result;
    }
    
    public int getPosition() {
        return mPos;
    }
    
    public boolean hasMore() {
        return mPos < mExpression.length();
    }
    
    private Token getNumber() {
        int start = mPos;
        int stop = start;
        
        char ch = mExpression.charAt(stop);
        while(mExpression.length() > stop && (Character.isDigit(ch) || ch == '.')) {
            ch = mExpression.charAt(stop++);
        }
        
        if (stop == start) {
            return null;
        }
        
        if (!Character.isDigit(ch)) {
            stop--;
        }
        
        if (start == stop) {
            return null;
        }
        
        if (mExpression.charAt(start) == '-' && stop - start == 1) {
            return null;
        }
        
        String value = mExpression.substring(start, stop);
        
        Token result = new Token(TokenType.Number, value, mPos);
        
        mPos = stop;
        return result;
    }
    
    private Token getOperator() {

        char ch = mExpression.charAt(mPos);
        TokenType t;
        switch(ch) {
            case '+': t = TokenType.Plus; break;
            case '-': t = TokenType.Minus; break;
            case '*': t = TokenType.Star; break;
            case '/': t = TokenType.Slash; break;
            case '^': t = TokenType.Caret; break;
            case '%': t = TokenType.Percent; break;
            case '(': t = TokenType.OpenParen; break;
            case ')': t = TokenType.CloseParen; break;
            case ':': t = TokenType.Colon; break;
            case ';': t = TokenType.Semi; break;
            case ',': t = TokenType.Comma; break;
            default: t = null;
        }
        
        if (t == null) {
            return null;
        }
        Token result = new Token(t, Character.toString(ch), mPos);

        mPos++;
        return result;
    }
    
    private Token getIdentifier() {
        int start = mPos;
        int stop = start;
        
        char ch = mExpression.charAt(stop);
        
        // identifiers must start with a letter.
        if (!Character.isLetter(ch)) {
            return null;
        }
        
        while(mExpression.length() > stop && (Character.isLetter(ch) || Character.isDigit(ch) || ch == ':' || ch == '!')) {
            ch = mExpression.charAt(stop++);
        }
        
        if (start == stop) {
            return null;
        }
        
        if (!Character.isLetter(ch) && !Character.isDigit(ch) && ch != ':' && ch != '!') {
            stop--;
        }
        
        String value = mExpression.substring(start, stop);
        Token result = new Token(TokenType.Identifier, value, mPos);

        mPos = stop;
        
        return result;
    }
    
    private Token getWhitespace() {
        int start = mPos;
        int stop = start;
        
        char ch = mExpression.charAt(stop);
        while(mExpression.length() > stop && Character.isWhitespace(ch)) {
            ch = mExpression.charAt(stop++);
        }

        if (start == stop) {
            return null;
        }
        
        if (!Character.isWhitespace(ch)) {
            stop--;
        }
        
        String value = mExpression.substring(start, stop);
        Token result = new Token(TokenType.Whitespace, value, mPos);
        mPos = stop;
        
        return result;
    }
}
