package org.rl337.economy;

import java.util.List;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Market;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.event.Event;

import com.google.inject.Inject;

public class Simulation implements SimulationProxy {
    @Inject 
    private EventLoop mEventLoop;
    @Inject
    private Market mMarket;
    @Inject
    private KeyFactory mKeyFactory;
    @Inject
    private EntityFactory mEntityFactory;
    
    public EntityKey addEntity(String name) {
        
        if (name == null || name.length() < 1) {
            return null;
        }
        
        Entity e = mEntityFactory.newEntity(name);
        
        return e.getKey();
    }
    
    public Entity removeEntity(EntityKey key) {
        return mEntityFactory.remove(key);
    }
    
    public List<Entity> listEntities() {
        return mEntityFactory.listEntities();
    }
    
    public int entityCount() {
        return mEntityFactory.size();
    }
    
    public void executeTick() {
        Tick tick = mKeyFactory.newKey(KeyType.Tick);

        mMarket.executeTick(tick);
        
        List<Entity> entities = listEntities();
        for(Entity entity : entities) {
            Event e = entity.getEvent(tick);
            
            // This entity died this turn. Remove it.
            if (!entity.isAlive()) {
                mEntityFactory.remove(entity.getKey());
            }
            
            if (e != null) {
                addEvent(e);
            }
        }
        
        
        int executed = mEventLoop.executeTick(tick);
        
        System.out.println("Executed " + executed + " events on tick " + tick);
    }

    public boolean addEvent(Event e) {
        return mEventLoop.addEvent(e);
    }

    @Override
    public Tick getCurrentTick() {
        return mKeyFactory.currentKey(KeyType.Tick);
    }

}
