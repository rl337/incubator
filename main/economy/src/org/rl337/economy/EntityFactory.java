package org.rl337.economy;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

public class EntityFactory {
    @Inject
    private KeyFactory mKeyFactory;
    
    private HashMap<String, Entity> mEntities;
    
    public EntityFactory() {
        mEntities = new HashMap<String, Entity>();
    }
    
    public Entity get(EntityKey key) {
        return mEntities.get(key.toString());
    }
    
    public boolean load(File file) {
        Type mapType = new TypeToken<HashMap<String, Entity>>(){}.getType();
        
        HashMap<String, Entity> result = SerializationUtil.load(mapType, file);
        if (result == null) {
            return false;
        }
        
        mEntities = result;
        
        return true;
    }
    
    public boolean save(File file) {
        return SerializationUtil.write(mEntities, file);
    }

    
    public Entity newEntity(String name) {
        EntityKey k = mKeyFactory.newKey(KeyType.Entity);
        Tick t = mKeyFactory.currentKey(KeyType.Tick);
        Entity e = new Entity(k, name, t);
        mEntities.put(k.toString(), e);
        
        return e;
    }
    
    public Entity remove(EntityKey key) {
        return mEntities.remove(key);
    }
    
    public int size() {
        return mEntities.size();
    }
    
    public List<Entity> listEntities() {
        return new ArrayList<Entity>(mEntities.values());
    }
}
