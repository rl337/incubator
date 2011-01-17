package org.rl337.economy;

import org.rl337.economy.data.Event;
import org.rl337.economy.data.EventLoopProxy;

import junit.framework.TestCase;

public class EventLoopTests extends TestCase {
    private EventLoop mEventLoop;

    public void setUp() {
        mEventLoop = new EventLoop();
    }

    public void testAddEventSimple() {
        TestEvent event = new TestEvent(3);

        assertTrue("Add at tick 3 should work.", mEventLoop.addEvent(event));
        assertFalse("Add of null should return false", mEventLoop.addEvent(null));

        assertEquals("Tick 0 should have no events.", 0, mEventLoop.executeNextTick());
        assertEquals("Tick 1 should have no events.", 0, mEventLoop.executeNextTick());

        assertFalse("Add of event with current tick should return false", mEventLoop.addEvent(new TestEvent(1)));
        assertFalse("Add of event with historic tick should return false", mEventLoop.addEvent(new TestEvent(0)));

        assertEquals("Tick 2 should have no events.", 0, mEventLoop.executeNextTick());
        assertEquals("Tick 3 should have 1 event.", 1, mEventLoop.executeNextTick());
        assertEquals("Tick 4 should have no events.", 0, mEventLoop.executeNextTick());

        assertTrue("Event Loop should now be idle", mEventLoop.isIdle());
        assertEquals("Test Event should have executed at 3.", 3, event.getExecutedTick());
    }

    private static class TestEvent implements Event {
        private long mExecutionTick;
        private long mExecutedTick;

        public TestEvent(long tick) {
            mExecutionTick = tick;
            mExecutedTick = -1;
        }

        @Override
        public void execute(EventLoopProxy p) {
            mExecutedTick = p.getCurrentTick();
        }

        @Override
        public long getExecuteOnTick() {
            return mExecutionTick;
        }

        public long getExecutedTick() {
            return mExecutedTick;
        }

    }
}
