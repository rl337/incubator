package org.rl337.economy.data;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Entity> mEntities;
    
    public Simulation() {
        mEntities = new ArrayList<Entity>();
    }
    
    public void addEntity(Entity e) {
        mEntities.add(e);
    }
    
    public List<Entity> getEntities() {
        return mEntities;
    }
    
    public int entityCount() {
        return mEntities.size();
    }

}
