package org.rl337.economy.event;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.event.AbstractEvent;
import org.rl337.economy.event.Event;
import org.rl337.economy.event.Event.EventException;

import junit.framework.TestCase;

public class AbstractEventTests extends TestCase {

    public void testExecute() throws Exception {
        TestSimulationProxy proxy = new TestSimulationProxy();
        TestEvent event = new TestEvent(proxy.getCurrentTick(), false);
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a null executed tick.", null, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        event.execute(proxy);
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick().getValue());
        assertTrue("Event should report success", event.success());
    }
    
    public void testExecuteWithException() throws Exception {
        TestSimulationProxy proxy = new TestSimulationProxy();
        TestEvent event = new TestEvent(proxy.getCurrentTick(), true);
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a null executed tick.", null, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        try {
            event.execute(proxy);
        } catch (EventException e) {
            // expected behavior
        }
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick().getValue());
        assertFalse("Event should report not success after exception thrown", event.success());
    }
    
    private static class TestSimulationProxy implements SimulationProxy {

        @Override
        public boolean addEvent(Event e) {
            return false;
        }

        @Override
        public Key getCurrentTick() {
            return new Key(KeyType.Tick, 0);
        }

        @Override
        public boolean addEntity(String entityName) {
            return false;
        }
        
    }
    
    private static class TestEvent extends AbstractEvent {
        private boolean mThrowsException;
        
        public TestEvent(Key tickToExecute, boolean except) {
            super(tickToExecute);
            mThrowsException = except;
        }

        @Override
        public void protectedExecute(SimulationProxy p) throws Exception {
            if (mThrowsException) {
                throw new Exception("Blah");
            }
        }
        
    }
}
