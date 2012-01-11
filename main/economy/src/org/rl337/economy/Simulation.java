package org.rl337.economy;

import java.io.File;
import java.util.List;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Market;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Simulation implements SimulationProxy {
    private static final Logger smLogger = LoggerFactory.getLogger(Simulation.class);
    
    @Inject 
    private EventLoop mEventLoop;
    @Inject
    private Market mMarket;
    @Inject
    private KeyFactory mKeyFactory;
    @Inject
    private EntityFactory mEntityFactory;
    @Inject
    private DirectoryNamingUtil mDirectoryNamingUtil;
    
    public boolean load() {
        
        // Our saves are stored in zero padded directories of the form:
        //   YYYYMMDD-HHmmss-SSSS
        // where SSSS represents milliseconds... this gives us an easily ordered
        // set of saves.  We want to load the latest save.
        File saveDirectory = mDirectoryNamingUtil.getLatestDateFormattedSubDirectory();
        if (saveDirectory == null) {
            smLogger.error("Could not determine a directory to load from.");
            return false;
        }
        
        File keyData = new File(saveDirectory, "keys.json");
        if (!mKeyFactory.load(keyData)) {
            smLogger.error("Could not load key data.");
            return false;
        }
        
        File entityData = new File(saveDirectory, "entities.json");
        if (!mEntityFactory.load(entityData)) {
            smLogger.error("Could not load entity data.");
            return false;
        }
        
        File eventData = new File(saveDirectory, "events.dat");
        if (!mEventLoop.load(eventData)) {
            smLogger.error("Could not load event loop state.");
            return false;
        }
        
        File marketData = new File(saveDirectory, "market.json");
        if (!mMarket.load(marketData)) {
            smLogger.error("Could not load current market state.");
            return false;
        }
        
        return true;
    }
    
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
            // This entity died this turn. Remove it.
            if (!entity.isAlive()) {
                mEntityFactory.remove(entity.getKey());
                continue;
            }
            
            Event e = entity.getEvent(tick);
            if (e != null) {
                addEvent(e);
            }
        }
        
        int executed = mEventLoop.executeTick(tick);
        int entityCount = entities.size();
        System.out.println("Executed " + executed + " events on tick " + tick + " for " + entityCount + " entities");
    }

    public boolean addEvent(Event e) {
        return mEventLoop.addEvent(e);
    }

    @Override
    public Tick getCurrentTick() {
        return mKeyFactory.currentKey(KeyType.Tick);
    }
    
    @Override
    public Entity getEntity(EntityKey ek) {
        return mEntityFactory.get(ek);
    }

}
