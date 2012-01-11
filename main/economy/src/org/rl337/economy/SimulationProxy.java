package org.rl337.economy;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.event.Event;

public interface SimulationProxy {
    boolean addEvent(Event e);
    EntityKey addEntity(String entityName);
    Tick getCurrentTick();
    Entity getEntity(EntityKey entityKey);
}
