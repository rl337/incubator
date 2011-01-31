package org.rl337.economy;

import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.data.entity.Entity;

public class EntityFactory {
    private KeyFactory mKeyFactory;
    
    public EntityFactory(KeyFactory keyFactory) {
        mKeyFactory = keyFactory;
    }
    
    public Entity newEntity(String name) {
        return new Entity(name, mKeyFactory.currentKey(KeyType.Tick));
    }
}
