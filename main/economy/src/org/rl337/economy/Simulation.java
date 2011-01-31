package org.rl337.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.data.Market;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.event.Event;

public class Simulation implements SimulationProxy {
    private HashMap<String, Entity> mEntities;
    private KeyFactory mKeyFactory;
    private EntityFactory mEntityFactory;
    private EventLoop mEventLoop;
    private Market mMarket;
    
    public Simulation() {
        mEntities = new HashMap<String, Entity>();
        mKeyFactory = new KeyFactory();
        mEntityFactory = new EntityFactory(mKeyFactory);
        mEventLoop = new EventLoop(this);
        mMarket = new Market(mKeyFactory);
    }
    
    public boolean addEntity(String name) {
        
        if (name == null || name.length() < 1) {
            return false;
        }
        if (mEntities.containsKey(name)) {
            return false;
        }
        
        Entity e = mEntityFactory.newEntity(name);
        mEntities.put(e.getName(), e);
        
        return true;
    }
    
    public Entity removeEntity(String name) {
        if (!mEntities.containsKey(name)) {
            return null;
        }
        
        return mEntities.remove(name);
    }
    
    public List<Entity> getEntities() {
        return new ArrayList<Entity>(mEntities.values());
    }
    
    public int entityCount() {
        return mEntities.size();
    }
    
    public void executeTick() {
        Key tick = mKeyFactory.newKey(KeyType.Tick);

        mMarket.executeTick(tick);
        
        Entity[] entities = mEntities.values().toArray(new Entity[mEntities.size()]);
        for(Entity entity : entities) {
            Event e = entity.getEvent(tick);
            
            // This entity died this turn. Remove it.
            if (!entity.isAlive()) {
                mEntities.remove(entity.getName());
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
    public Key getCurrentTick() {
        return mKeyFactory.currentKey(KeyType.Tick);
    }

}
