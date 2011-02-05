package org.rl337.economy.data;

import java.io.File;

import junit.framework.TestCase;

import org.rl337.economy.EntityFactory;
import org.rl337.economy.KeyFactory;
import org.rl337.economy.SerializationUtil;
import org.rl337.economy.data.entity.Entity;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class EntityTest extends TestCase {
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
    

    public void testEntityLoadAndSave() {
        Entity e = mEntityFactory.newEntity("blargle");
        
        assertTrue("Entity write should return true.", SerializationUtil.write(e, mFile));
        
        Entity gotEntity = SerializationUtil.load(Entity.class, mFile);
        
        assertEntityEquals(e, gotEntity);
    }
    

    
    public void assertEntityEquals(Entity a, Entity b) {
        assertEquals("names should be same.", a.getName(), b.getName());
        assertEquals("ID should be the same", a.getKey(), b.getKey());
        assertEquals("Happiness should be the same", a.getHappiness(), b.getHappiness());
        assertEquals("Credits should be the same", a.getHappiness(), b.getHappiness());
        assertEquals("Aliveness should be the same", a.isAlive(), b.isAlive());
    }
}
