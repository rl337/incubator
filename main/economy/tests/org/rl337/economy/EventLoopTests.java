package org.rl337.economy;


import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.event.Event;

import junit.framework.TestCase;

public class EventLoopTests extends TestCase {
    private EventLoop mEventLoop;
    private SimulationProxy mSimulationProxy;
    private KeyFactory mKeyFactory;
    
    public void setUp() {
        mKeyFactory = new KeyFactory();
        mSimulationProxy = new TestSimulationProxy(mKeyFactory);
        mEventLoop = new EventLoop(mSimulationProxy);
    }

    public void testAddEventSimple() {
        System.out.println("Start of test");
        TestEvent event = new TestEvent(new Key(KeyType.Tick, 4));

        assertTrue("Add at tick 3 should work.", mEventLoop.addEvent(event));
        assertFalse("Add of null should return false", mEventLoop.addEvent(null));

        assertEquals("Tick 1 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 2 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertFalse("Add of event with current tick should return false", mEventLoop.addEvent(new TestEvent(new Key(KeyType.Tick, 2))));
        assertFalse("Add of event with historic tick should return false", mEventLoop.addEvent(new TestEvent(new Key(KeyType.Tick, 1))));

        assertEquals("Tick 3 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 4 should have 1 event.", 1, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));
        assertEquals("Tick 5 should have no events.", 0, mEventLoop.executeTick(mKeyFactory.newKey(KeyType.Tick)));

        assertTrue("Event Loop should now be idle", mEventLoop.isIdle());
        assertEquals("Test Event should have executed at 4.", 4, event.getExecutedTick().getValue());
    }

    private static class TestEvent implements Event {
        private Key mExecutionTick;
        private Key mExecutedTick;

        public TestEvent(Key tick) {
            mExecutionTick = tick;
            mExecutedTick = null;
        }

        @Override
        public void execute(SimulationProxy p) {
            mExecutedTick = p.getCurrentTick();
        }

        @Override
        public Key getExecuteOnTick() {
            return mExecutionTick;
        }

        public Key getExecutedTick() {
            return mExecutedTick;
        }

    }
    
    private static class TestSimulationProxy implements SimulationProxy {
        private KeyFactory mKeyFactory;
        
        public TestSimulationProxy(KeyFactory f) {
            mKeyFactory = f;
        }

        @Override
        public boolean addEvent(Event e) {
            return false;
        }

        @Override
        public Key getCurrentTick() {
            return mKeyFactory.currentKey(KeyType.Tick);
        }

        @Override
        public boolean addEntity(String entityName) {
            // TODO Auto-generated method stub
            return false;
        }
        
    }
}
