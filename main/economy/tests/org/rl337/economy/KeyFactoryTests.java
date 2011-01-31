package org.rl337.economy;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;

import junit.framework.TestCase;

public class KeyFactoryTests extends TestCase {
    private KeyFactory mFactory;
    
    public void setUp() {
        mFactory = new KeyFactory();
    }

    public void testSimpleNewKeys() {
        Key k = mFactory.newKey(KeyType.Bid);
        
        assertEquals("Key type should be Bid", KeyType.Bid, k.getKeyType());
        assertEquals("The very first key should be 1", 1, k.getValue());
        
        Key l = mFactory.newKey(KeyType.Bid);
        assertEquals("Key type should be Bid", KeyType.Bid, l.getKeyType());
        assertEquals("This key should be 2", 2, l.getValue());
    }
    
}
