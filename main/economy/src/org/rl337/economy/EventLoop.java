package org.rl337.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rl337.economy.data.Event;
import org.rl337.economy.data.EventLoopProxy;
import org.rl337.economy.data.Event.EventException;

public class EventLoop implements EventLoopProxy {
    private HashMap<Long, List<Event>> mEventMap;
    private long mTick;

    public EventLoop() {
        mEventMap = new HashMap<Long, List<Event>>();
        mTick = 0;
    }

    public void setTick(long tick) {
        mTick = tick;
    }

    public boolean addEvent(Event event) {
        if (event == null) {
            return false;
        }

        long eventTick = event.getExecuteOnTick();
        if (eventTick <= mTick) {
            return false;
        }

        if (!mEventMap.containsKey(eventTick)) {
            mEventMap.put(eventTick, new ArrayList<Event>());
        }

        List<Event> eventList = mEventMap.get(eventTick);
        eventList.add(event);

        return true;
    }

    public int executeNextTick() {
        if (!mEventMap.containsKey(mTick)) {
            mTick++;
            return 0;
        }

        List<Event> eventsToRun = mEventMap.remove(mTick);

        int eventsExecuted = 0;
        for (Event event : eventsToRun) {
            try {
                event.execute(this);
            } catch (EventException e) {
                // swallow for now.
            }
            eventsExecuted++;
        }

        mTick++;
        return eventsExecuted;
    }

    public long getCurrentTick() {
        return mTick;
    }

    public boolean isIdle() {
        return mEventMap.isEmpty();
    }

}
