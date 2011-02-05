package org.rl337.economy;

import java.io.File;
import java.util.Random;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;

import junit.framework.TestCase;

public class KeyFactoryTests extends TestCase {
    private static final Random smRandom = new Random();
    private KeyFactory mFactory;
    private File mFile;
    
    public void setUp() throws Exception {
        mFactory = new KeyFactory();
        mFile = File.createTempFile("KeyFactoryTests", ".txt");
        
    }
    
    public void tearDown() {
        mFile.delete();
    }

    public void testSimpleNewKeys() {
        Key k = mFactory.newKey(KeyType.Bid);
        
        assertEquals("Key type should be Bid", KeyType.Bid, k.getKeyType());
        assertEquals("The very first key should be 1", 1, k.getValue());
        
        Key l = mFactory.newKey(KeyType.Bid);
        assertEquals("Key type should be Bid", KeyType.Bid, l.getKeyType());
        assertEquals("This key should be 2", 2, l.getValue());
    }
    
    public void testLoadAndSave() {
        int bidCount = smRandom.nextInt(100);
        int entityCount = smRandom.nextInt(100);
        int tickCount = smRandom.nextInt(100);
        
        for(int i = 0; i < bidCount; i++) mFactory.newKey(KeyType.Bid);
        for(int i = 0; i < entityCount; i++) mFactory.newKey(KeyType.Entity);
        for(int i = 0; i < tickCount; i++) mFactory.newKey(KeyType.Tick);
        
        assertTrue("save() should return true.", mFactory.save(mFile));
        
        KeyFactory newFactory = new KeyFactory();
        
        assertTrue("load() should have returned true", newFactory.load(mFile));
        
        assertEquals("bid key should be " + bidCount, bidCount, newFactory.currentKey(KeyType.Bid).getValue());
        assertEquals("entity key should be " + entityCount, entityCount, newFactory.currentKey(KeyType.Entity).getValue());
        assertEquals("tick key should be " + tickCount, tickCount, newFactory.currentKey(KeyType.Tick).getValue());
        assertEquals("unknown key should be 0", 0, newFactory.currentKey(KeyType.Unknown).getValue());
    }
    
}
