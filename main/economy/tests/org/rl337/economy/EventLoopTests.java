package org.rl337.economy;


import junit.framework.TestCase;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.event.Event;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class EventLoopTests extends TestCase {
    private EventLoop mEventLoop;
    private KeyFactory mKeyFactory;
    
    public void setUp() {
        
        Injector testInjector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(KeyFactory.class).asEagerSingleton();
                    bind(SimulationProxy.class).to(TestSimulationProxy.class);
                }
            }
        );
        
        mEventLoop = testInjector.getInstance(EventLoop.class);
        mKeyFactory = testInjector.getInstance(KeyFactory.class);
    }

    public void testAddEventSimple() {
        System.out.println("Start of test");
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
        assertEquals("Test Event should have executed at 4.", 4, event.getExecutedTick().getValue());
    }

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
            return null;
        }
        
    }
}
