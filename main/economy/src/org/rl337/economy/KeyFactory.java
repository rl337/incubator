package org.rl337.economy;

import java.util.HashMap;

public class KeyFactory {
    private HashMap<KeyType, KeyCounter> mCounters;
    
    public KeyFactory() {
        mCounters = new HashMap<KeyType, KeyCounter>();
    }
    
    private KeyCounter getKeyCounter(KeyType kt) {
        if (kt == null) {
            return null;
        }

        KeyCounter counter;
        if (!mCounters.containsKey(kt)) {
            counter = new KeyCounter(0);
            mCounters.put(kt, counter);
        } else {
            counter = mCounters.get(kt);
        }
        
        return counter;
    }
    
    public Key newKey(KeyType kt) {
        KeyCounter counter = getKeyCounter(kt);
        if (counter == null) {
            return null;
        }
        long value = counter.next();
        return new Key(kt, value);
    }
    
    public Key currentKey(KeyType kt) {
        KeyCounter counter = getKeyCounter(kt);
        if (counter == null) {
            return null;
        }
                
        return new Key(kt, counter.current());
    }
    
    private static class KeyCounter {
        private long mCounter;
        private long mIncrement;
        private volatile Object mLock;
        
        private KeyCounter(long start, long incBy) {
            mLock = new Object();
            mCounter = start;
            mIncrement = incBy;
        }
        
        private KeyCounter(long start) {
            this(start, 1);
        }
        
        public long next() {
            synchronized (mLock) {
                return mCounter+=mIncrement;
            }
        }
        
        public long current() {
            return mCounter;
        }
    }
    
    public static enum KeyType implements Comparable<KeyType> {
        Tick("TIC"),
        Bid("BID"),
        Entity("ENT");
        
        private String mPrefix;
        
        private KeyType(String prefix) {
            mPrefix = prefix;
        }
        
        public String getPrefix() {
            return mPrefix;
        }
        
        public boolean equals(KeyType k) {
            return mPrefix.equals(k.getPrefix());
        }
    }
    
    public static class Key implements Comparable<Key> {
        private long mValue;
        private KeyType mKeyType;
        
        public Key(KeyType k, long i) {
            mValue = i;
            mKeyType = k;
        }
        
        public KeyType getKeyType() {
            return mKeyType;
        }
        
        public long getValue() {
            return mValue;
        }
        
        @Override
        public int compareTo(Key k) {
            
            int typeCompare = mKeyType.compareTo(k.getKeyType());
            if (typeCompare != 0) return typeCompare;
            
            long delta = mValue - k.getValue();
            if (delta < 0) return -1;
            if (delta > 0) return 1;
            return 0;
        }
        
        public boolean equals(Object o) {
            Key k = (Key) o;
            if (mKeyType != k.getKeyType()) {
                return false;
            }
            
            if (mValue != k.getValue()) {
                return false;
            }
            
            return true;
        }

        public int hashCode() {
            return (int) (mKeyType.hashCode() + (int) (Math.abs(mValue % Integer.MAX_VALUE) - 1));
        }
        
        public String toString() {
            return mKeyType.getPrefix() + Long.toString(mValue);
        }
    }
}
