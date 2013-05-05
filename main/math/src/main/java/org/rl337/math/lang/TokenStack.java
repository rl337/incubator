package org.rl337.math.lang;

import java.util.ArrayList;

public class TokenStack {
    private ArrayList<Token> mData;
    
    public TokenStack() {
        mData = new ArrayList<Token>();
    }
    
    public void push(Token t) {
        mData.add(t);
    }
    
    public Token pop() {
        int size = mData.size();
        if (size < 1) {
            return null;
        }
        
        return mData.remove(size - 1);
    }
    
    public Token shift() {
        int size = mData.size();
        if (size < 1) {
            return null;
        }
        
        return mData.remove(0);
    }
    
    public void unshift(Token t) {
        mData.add(0, t);
    }
    
    public Token peekTop() {
        int size = mData.size();
        if (size < 1) {
            return null;
        }
        
        return mData.get(size - 1);
    }
    
    public Token peekBottom() {
        int size = mData.size();
        if (size < 1) {
            return null;
        }
        
        return mData.get(0);
    }
    
    public TokenType peekType() {
        int size = mData.size();
        if (size < 1) {
            return null;
        }
        
        return mData.get(size - 1).getType();
    }
    
    public Token get(int index) {
        return mData.get(index);
    }
    
    public boolean isEmpty() {
        return mData.isEmpty();
    }
    
    public int size() {
        return mData.size();
    }

}
