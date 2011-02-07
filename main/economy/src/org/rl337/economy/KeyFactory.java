package org.rl337.economy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class KeyFactory {
    private static final Logger smLogger = LoggerFactory.getLogger(KeyFactory.class);
    
    private HashMap<KeyType, KeyCounter> mCounters;
    
    public KeyFactory() {
        mCounters = new HashMap<KeyType, KeyCounter>();
    }
    
    public boolean load(File file) {
        
        Type mapType = new TypeToken<HashMap<KeyType, KeyCounter>>(){}.getType();
        
        HashMap<KeyType, KeyCounter> result = SerializationUtil.load(mapType, file);
        if (result == null) {
            return false;
        }
        
        mCounters = result;
        
        return true;
    }
    
    public boolean save(File file) {
        return SerializationUtil.write(mCounters, file);
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
    
    public <T extends Key> T newKey(KeyType kt) {
        KeyCounter counter = getKeyCounter(kt);
        if (counter == null) {
            return null;
        }
        long value = counter.next();
        if ((value / counter.getIncrementBy()) % 10000 == 0) {
            smLogger.info(kt.getPrefix() + " generated key " + value);
        }

        return KeyType.getInstance(kt, value);
    }
    
    public <T extends Key> T currentKey(KeyType kt) {
        KeyCounter counter = getKeyCounter(kt);
        if (counter == null) {
            return null;
        }
        
        long value = counter.current();
        
        return KeyType.getInstance(kt, value);
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
        
        public long getIncrementBy() {
            return mIncrement;
        }
    }
    
    public static enum KeyType implements Comparable<KeyType> {
        Unknown("XXX", UnknownKey.class),
        Tick("TIC", Tick.class),
        Bid("BID", BidKey.class),
        Entity("ENT", EntityKey.class);
        
        private String mPrefix;
        private Class<? extends Key> mClass;
        
        private KeyType(String prefix, Class<? extends Key> clazz) {
            mPrefix = prefix;
            mClass = clazz;
        }
        
        public String getPrefix() {
            return mPrefix;
        }
        
        public boolean equals(KeyType k) {
            return mPrefix.equals(k.getPrefix());
        }
        
        public Class<? extends Key> getKeyClass() {
            return mClass;
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
        
        @SuppressWarnings("unchecked")
        private static <T extends Key> T getInstance(KeyType kt, long value) {
            Class<T> clazz = (Class<T>) kt.getKeyClass();
            try {
                //Constructor<T> c = clazz.getConstructor(new Class<?>[] {long.class});
                Constructor<T> c = clazz.getConstructor(long.class);
                if (c == null) {
                    return null;
                }

                return c.newInstance(value);
            } catch (SecurityException e) {
                smLogger.error("SecurityException instantiating " + kt.mPrefix, e);
            } catch (NoSuchMethodException e) {
                smLogger.error("NoSuchMethodException instantiating " + kt.mPrefix + " " + clazz.getName(), e);
            } catch (IllegalArgumentException e) {
                smLogger.error("IllegalArgumentException instantiating " + kt.mPrefix, e);
            } catch (InstantiationException e) {
                smLogger.error("InstantiationException instantiating " + kt.mPrefix, e);
            } catch (IllegalAccessException e) {
                smLogger.error("IllegalAccessException instantiating " + kt.mPrefix, e);
            } catch (InvocationTargetException e) {
                smLogger.error("InvocationTargetException instantiating " + kt.mPrefix, e);
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
            return mKeyType.getPrefix() + "-" + Long.toString(mValue);
        }
    }
    
    public static class Tick extends Key {
        public Tick(){
            this(-1);
        }

        public Tick(long tick) {
            super(KeyType.Tick, tick);
        }

        public Tick getFutureTick(int i) {
            return new Tick(getValue() + i);
        }
    }
    
    public static class BidKey extends Key {
        public BidKey(){
            this(-1);
        }

        public BidKey(long bidId) {
            super(KeyType.Bid, bidId);
        }
    }
    
    public static class EntityKey extends Key {
        public EntityKey(){
            this(-1);
        }
        
        public EntityKey(long entityId) {
            super(KeyType.Entity, entityId);
        }
    }
    
    public static class UnknownKey extends Key {
        public UnknownKey(){
            this(-1);
        }

        public UnknownKey(long id) {
            super(KeyType.Unknown, id);
        }
    }
}
