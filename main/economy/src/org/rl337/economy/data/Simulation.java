package org.rl337.economy.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simulation {
    private HashMap<String, Entity> mEntities;
    
    public Simulation() {
        mEntities = new HashMap<String, Entity>();
    }
    
    public boolean addEntity(Entity e) {
        if (e == null) {
            return false;
        }
        
        String name = e.getName();
        if (name == null || name.length() < 1) {
            return false;
        }
        
        if (mEntities.containsKey(name)) {
            return false;
        }
        
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

}
