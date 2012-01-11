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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.util.Types;

public class EntityFactory {
    private static final Logger smLogger = LoggerFactory.getLogger(EntityFactory.class);
    
    @Inject
    private KeyFactory mKeyFactory;
    
    @SuppressWarnings("unchecked") @Inject @Named("entityFactory.entityClass")
    private Class mEntityClass;
    
    private HashMap<String, Entity> mEntities;
    
    public EntityFactory() {
        mEntities = new HashMap<String, Entity>();
    }
    
    public Entity get(EntityKey key) {
        return mEntities.get(key.toString());
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> boolean load(File file) {
        Type mapType = Types.newParameterizedType(HashMap.class, String.class, mEntityClass);
        
        HashMap<String, T> result = SerializationUtil.loadJSON(mapType, file);
        if (result == null) {
            smLogger.warn("Could not load file: " + file);
            return false;
        }
        
        mEntities = (HashMap<String, Entity>) result;
        
        return true;
    }
    
    public boolean save(File file) {
        return SerializationUtil.writeJSON(mEntities, file);
    }

    
    public Entity newEntity(String name) {
        EntityKey k = mKeyFactory.newKey(KeyType.Entity);
        Tick t = mKeyFactory.currentKey(KeyType.Tick);
        
        Entity entity = null;
        try {
            entity = (Entity) mEntityClass.newInstance();
            entity.setKey(k);
            entity.setName(name);
            entity.setBornOnTick(t);
            mEntities.put(k.toString(), entity);
        } catch (InstantiationException e) {
            smLogger.error("Could not instantiate entity of type: " + mEntityClass.getName(), e);
        } catch (IllegalAccessException e) {
            smLogger.error("Could not access entity of type: " + mEntityClass.getName(), e);
        }
        
        return entity;
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
