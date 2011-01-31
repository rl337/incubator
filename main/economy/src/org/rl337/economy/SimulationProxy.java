package org.rl337.economy;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.event.Event;

public interface SimulationProxy {
    boolean addEvent(Event e);
    boolean addEntity(String entityName);
    Key getCurrentTick();
}
