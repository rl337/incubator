package org.rl337.economy.data;

import org.rl337.economy.data.Event.EventException;

import junit.framework.TestCase;

public class AbstractEventTests extends TestCase {

    public void testExecute() throws Exception {
        TestEvent event = new TestEvent(0, false);
        TestEventLoopProxy proxy = new TestEventLoopProxy();
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a -1 executed tick.", -1, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        event.execute(proxy);
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick());
        assertTrue("Event should report success", event.success());
    }
    
    public void testExecuteWithException() throws Exception {
        TestEvent event = new TestEvent(0, true);
        TestEventLoopProxy proxy = new TestEventLoopProxy();
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a -1 executed tick.", -1, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        try {
            event.execute(proxy);
        } catch (EventException e) {
            // expected behavior
        }
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick());
        assertFalse("Event should report not success after exception thrown", event.success());
    }
    
    private static class TestEventLoopProxy implements EventLoopProxy {

        @Override
        public boolean addEvent(Event e) {
            return false;
        }

        @Override
        public long getCurrentTick() {
            return 0;
        }
        
    }
    
    private static class TestEvent extends AbstractEvent {
        private boolean mThrowsException;
        
        public TestEvent(long tickToExecute, boolean except) {
            super(tickToExecute);
            mThrowsException = except;
        }

        @Override
        public void protectedExecute(EventLoopProxy p) throws Exception {
            if (mThrowsException) {
                throw new Exception("Blah");
            }
        }
        
    }
}
