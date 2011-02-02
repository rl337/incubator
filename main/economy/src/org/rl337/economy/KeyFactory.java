package org.rl337.economy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyFactory {
    private static final Logger smLogger = LoggerFactory.getLogger(KeyFactory.class);
    
    @Expose @SerializedName("counters")
    private HashMap<KeyType, KeyCounter> mCounters;
    
    public KeyFactory() {
        mCounters = new HashMap<KeyType, KeyCounter>();
    }
    
    public static KeyFactory loadFile(File file) {
        Gson gson = new Gson();
        
        try {
        FileReader reader = new FileReader(file);
            KeyFactory result = gson.fromJson(reader, KeyFactory.class);
            return result;
        } catch (IOException e) {
            return null;
        }
        
    }
    
    public static boolean save(File file, KeyFactory factory) {
        if (file == null) {
            return false;
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String sb = gson.toJson(factory);
        
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            smLogger.error("Could not write to file: " + file.getPath());
            return false;
        }
        
        return true;
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
        @Expose @SerializedName("value")
        private long mCounter;

        @Expose @SerializedName("increment_by")
        private long mIncrement;
        private volatile Object mLock;
        
        private KeyCounter() {
            mLock = new Object();
            mIncrement = 1;
            mCounter = 0;
        }
        
        private KeyCounter(long start, long incBy) {
            this();
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
        Unknown("XXX"),
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
        
        public static KeyType parseKeyType(String prefix) {
            if (prefix == null || prefix.length() < 1) {
                return null;
            }
            
            String uc = prefix.toUpperCase();
            for(KeyType kt : values()) {
                if (uc.equals(kt.getPrefix())) {
                    return kt;
                }
            }
            
            return null;
        }
    }
    
    public static class Key implements Comparable<Key> {
        @Expose @SerializedName("v")
        private long mValue;
        @Expose @SerializedName("k")
        private KeyType mKeyType;
        
        // Create an empty Key.  This exists so that we can gson deserialize this type
        public Key() {
            this(KeyType.Unknown, 0);
        }
        
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
