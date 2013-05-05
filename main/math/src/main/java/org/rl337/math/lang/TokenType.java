package org.rl337.math.lang;

public enum TokenType {
    //
    Value(0, false, false),
    ValueList(0, false, false),
    ValueRange(0, false, false),
    
    // These are primitives
    Number(0, false, false),
    Identifier(0, false, false),
    Plus(10, true, true),
    Minus(10, true, true),
    Star(20, true, true),
    Slash(20, true, true),
    Caret(30, true, true),
    Percent(30, true, true),
    OpenParen(40, false, true),
    CloseParen(0, true, false),
    Comma(40, true, true),
    Colon(50, true, true),
    Semi(60, true, true),
    Whitespace(0, false, false),
    ;
    
    private int mPriority;
    private boolean mLParam;
    private boolean mRParam;
    
    TokenType(int priority, boolean expectsLParam, boolean expectsRParam) {
        mPriority = priority;
        mLParam = expectsLParam;
        mRParam = expectsRParam;
    }
    
    public int getPriority() {
        return mPriority;
    }
    
    public boolean expectsLeftParam() {
        return mLParam;
    }
    
    
    public boolean expectsRightParam() {
        return mRParam;
    }
}
