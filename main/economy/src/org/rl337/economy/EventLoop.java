package org.rl337.economy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.event.Event;
import org.rl337.economy.event.Event.EventException;

import com.google.inject.Inject;

public class EventLoop {
    private HashMap<Key, List<Event>> mEventMap;
    
    @Inject
    private SimulationProxy mSimulationProxy;

    @Inject
    public EventLoop() {
        mEventMap = new HashMap<Key, List<Event>>();
    }

    public boolean addEvent(Event event) {
        if (event == null) {
            return false;
        }
        
        Tick executeOnTick = event.getExecuteOnTick();
        Tick currentTick = mSimulationProxy.getCurrentTick();
        if (currentTick.getValue() >= executeOnTick.getValue()) {
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
        Tick currentTick = mSimulationProxy.getCurrentTick();
        
        if (tick == null || tick.getValue() < currentTick.getValue()) {
            return 0;
        }
        
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

    public Tick getCurrentTick() {
        return mSimulationProxy.getCurrentTick();
    }

    public boolean isIdle() {
        return mEventMap.isEmpty();
    }

    public <T extends Entity> boolean load(File file) {
        HashMap<Key, List<Event>> eventMap = SerializationUtil.loadBinary(file);
        
        if (eventMap == null) {
            return false;
        }
        
        mEventMap = eventMap;
        return true;
    }
    
    public boolean save(File file) {
        return SerializationUtil.writeBinary(mEventMap, file);
    }


}
