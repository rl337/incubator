package org.rl337.economy;

import java.io.File;

import junit.framework.TestCase;

import org.rl337.economy.data.entity.Entity;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class EntityFactoryTests extends TestCase {
    private File mFile;
    private EntityFactory mEntityFactory;
    private Injector mInjector;
    
    public void setUp() throws Exception {
        
        mInjector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(KeyFactory.class).asEagerSingleton();
                }
            }
        );
        
        mEntityFactory = mInjector.getInstance(EntityFactory.class);
        mFile = File.createTempFile("EntityFactoryTests", ".txt");
    }
    
    public void tearDown() throws Exception {
        mFile.delete();
    }
    
    public void testCreateAndGetEntity() {
        Entity entity = mEntityFactory.newEntity("blargle");
        
        Entity gotEntity = mEntityFactory.get(entity.getKey());
        assertEntityEquals(entity, gotEntity);
    }
    
    public void testLoadAndSave() {
        Entity entity1 = mEntityFactory.newEntity("blargle");
        Entity entity2 = mEntityFactory.newEntity("flargle");
        Entity entity3 = mEntityFactory.newEntity("glargle");
        
        assertTrue("Saving should have returned true.", mEntityFactory.save(mFile));
        
        EntityFactory newFactory = mInjector.getInstance(EntityFactory.class);
        assertTrue("New instance should not be the same as old instance.", mEntityFactory != newFactory);
        assertTrue("loading should return true", newFactory.load(mFile));
        
        Entity entity1b = mEntityFactory.get(entity1.getKey());
        Entity entity2b = mEntityFactory.get(entity2.getKey());
        Entity entity3b = mEntityFactory.get(entity3.getKey());
        
        assertEntityEquals(entity1, entity1b);
        assertEntityEquals(entity2, entity2b);
        assertEntityEquals(entity3, entity3b);
    }

    
    public void assertEntityEquals(Entity a, Entity b) {
        assertTrue("a and b should not be the same instance.", a == b);
        assertEquals("names should be same.", a.getName(), b.getName());
        assertEquals("ID should be the same", a.getKey(), b.getKey());
        assertEquals("Happiness should be the same", a.getHappiness(), b.getHappiness());
        assertEquals("Credits should be the same", a.getHappiness(), b.getHappiness());
        assertEquals("Aliveness should be the same", a.isAlive(), b.isAlive());
    }

}
