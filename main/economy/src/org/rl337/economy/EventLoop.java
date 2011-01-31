package org.rl337.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.event.Event;
import org.rl337.economy.event.Event.EventException;

public class EventLoop {
    private HashMap<Key, List<Event>> mEventMap;
    private Key mCurrentTick;
    private SimulationProxy mSimulationProxy;

    public EventLoop(SimulationProxy simulationProxy) {
        mEventMap = new HashMap<Key, List<Event>>();
        mCurrentTick = simulationProxy.getCurrentTick();
        mSimulationProxy = simulationProxy;
    }

    public boolean addEvent(Event event) {
        if (event == null) {
            return false;
        }
        
        Key executeOnTick = event.getExecuteOnTick();
        if (mCurrentTick.getValue() >= executeOnTick.getValue()) {
            return false;
        }

        if (!mEventMap.containsKey(executeOnTick)) {
            mEventMap.put(executeOnTick, new ArrayList<Event>());
        }

        List<Event> eventList = mEventMap.get(executeOnTick);
        eventList.add(event);

        return true;
    }

    public int executeTick(Key tick) {
        if (tick == null || tick.getValue() < mCurrentTick.getValue()) {
            return 0;
        }
        
        mCurrentTick = tick;
        if (!mEventMap.containsKey(tick)) {
            return 0;
        }

        List<Event> eventsToRun = mEventMap.remove(tick);

        int eventsExecuted = 0;
        for (Event event : eventsToRun) {
            try {
                event.execute(mSimulationProxy);
            } catch (EventException e) {
                // swallow for now.
            }
            eventsExecuted++;
        }

        return eventsExecuted;
    }

    public Key getCurrentTick() {
        return mCurrentTick;
    }

    public boolean isIdle() {
        return mEventMap.isEmpty();
    }

}
