package org.rl337.economy;


import java.io.File;

import junit.framework.TestCase;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.data.entity.MarketUserImpl;
import org.rl337.economy.event.Event;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class EventLoopTests extends TestCase {
    private File mFile;
    private EventLoop mEventLoop;
    private KeyFactory mKeyFactory;
    private Injector mInjector;
    
    public void setUp() throws Exception {
        
        mInjector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(KeyFactory.class).asEagerSingleton();
                    bind(SimulationProxy.class).to(TestSimulationProxy.class);
                    bind(EntityFactory.class).asEagerSingleton();
                    bind(Class.class).annotatedWith(Names.named("entityFactory.entityClass")).toInstance(MarketUserImpl.class);
                }
            }
        );
        
        mEventLoop = mInjector.getInstance(EventLoop.class);
        mKeyFactory = mInjector.getInstance(KeyFactory.class);
        mFile = File.createTempFile("EntityFactoryTests", ".txt");
    }
    
    public void tearDown() throws Exception {
        mFile.delete();
    }

    public void testAddEventSimple() {
        TestEvent event = new TestEvent(new Tick(4));

        assertTrue("Add at tick 3 should work.", mEventLoop.addEvent(event));
        assertFalse("Add of null should return false", mEventLoop.addEvent(null));

        assertEquals("Tick 1 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 2 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertFalse("Add of event with current tick should return false", mEventLoop.addEvent(new TestEvent(new Tick(2))));
        assertFalse("Add of event with historic tick should return false", mEventLoop.addEvent(new TestEvent(new Tick(1))));

        assertEquals("Tick 3 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 4 should have 1 event.", 1, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 5 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertTrue("Event Loop should now be idle", mEventLoop.isIdle());
        
        // TODO: this shouldn't rely on events being mutable. 
        assertEquals("Test Event should have executed at 4.", 4, event.getExecutedTick().getValue());
    }
    
    public void testLoadAndSave() {
        TestEvent event = new TestEvent(new Tick(4));

        assertTrue("Add at tick 3 should work.", mEventLoop.addEvent(event));
        assertFalse("Add of null should return false", mEventLoop.addEvent(null));
        
        // Ok. Now we're going to save and load... then run the event on the *new* event loop.
        assertTrue("Saving should have returned true.", mEventLoop.save(mFile));
        
        EventLoop newLoop = mInjector.getInstance(EventLoop.class);
        assertTrue("newLoop should be idle before loading", newLoop.isIdle());
        assertTrue("Loading should have returned true", newLoop.load(mFile));
        assertFalse("newLoop should no longer be idle.", newLoop.isIdle());

        assertEquals("Tick 1 should have no events.", 0, newLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 2 should have no events.", 0, newLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertFalse("Add of event with current tick should return false", newLoop.addEvent(new TestEvent(new Tick(2))));
        assertFalse("Add of event with historic tick should return false", newLoop.addEvent(new TestEvent(new Tick(1))));

        assertEquals("Tick 3 should have no events.", 0, newLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 4 should have 1 event.", 1, newLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 5 should have no events.", 0, newLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertTrue("Event Loop should now be idle", newLoop.isIdle());
        
    }

    @SuppressWarnings("serial")
    private static class TestEvent implements Event {
        private Tick mExecutionTick;
        private Tick mExecutedTick;

        public TestEvent(Tick tick) {
            mExecutionTick = tick;
            mExecutedTick = null;
        }

        @Override
        public void execute(SimulationProxy p) {
            mExecutedTick = p.getCurrentTick();
        }

        @Override
        public Tick getExecuteOnTick() {
            return mExecutionTick;
        }

        public Key getExecutedTick() {
            return mExecutedTick;
        }

    }
    
    private static class TestSimulationProxy implements SimulationProxy {
        @Inject
        private KeyFactory mKeyFactory;
        @Inject
        private EntityFactory mEntityFactory;
        
        @Override
        public boolean addEvent(Event e) {
            return false;
        }

        @Override
        public Tick getCurrentTick() {
            return mKeyFactory.currentKey(KeyType.Tick);
        }

        @Override
        public EntityKey addEntity(String entityName) {
            return mEntityFactory.newEntity(entityName).getKey();
        }

        @Override
        public Entity getEntity(EntityKey entityKey) {
            return mEntityFactory.get(entityKey);
        }
        
    }
}
