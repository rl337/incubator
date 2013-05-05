package org.rl337.math.lang;


public class Token {
    private TokenType mType;
    private String mValue;
    private int mPos;
    
    public Token(TokenType type, String value, int pos) {
        mType = type;
        mValue = value;
        mPos = pos;
    }
    
    public Token(Token t, int pos) {
        mType = t.getType();
        mValue = t.getValue();
        mPos = pos;
    }

    public TokenType getType() {
        return mType;
    }
    
    public String getValue() {
        return mValue;
    }
    
    public int getPos() {
        return mPos;
    }
    
    public String toString() {
        return mType.toString() + "(" + mValue + ":" + mPos + ")";
    }
}
