package org.rl337.economy.event;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;

public abstract class AbstractEvent implements Event {
    private static final long serialVersionUID = -9096035749027033628L;
    private Tick mExecuteOnTick;
    private Tick mExecutedTick;
    private boolean mSuccess;
    private EntityKey mEntityKey;
    
    public AbstractEvent(EntityKey key, Tick tickToExecute) {
        mExecuteOnTick = tickToExecute;
        mExecutedTick = null;
        mSuccess = false;
        mEntityKey = key;
    }

    @Override
    public Tick getExecuteOnTick() {
        return mExecuteOnTick;
    }
    
    public Tick getExecutedOnTick() {
        return mExecutedTick;
    }
    
    public EntityKey getEntityKey() {
        return mEntityKey;
    }
    
    public boolean success() {
        return mSuccess;
    }
    
    public boolean executed() {
        return mExecutedTick != null;
    }
    
    public void execute(SimulationProxy p) throws EventException {
        mExecutedTick = p.getCurrentTick();
        Entity entity = p.getEntity(mEntityKey);
        if (entity == null) {
            mSuccess = false;
            throw new EventException("Invalid entity key on event.");
        }
        
        try {
            protectedExecute(entity, p);
        } catch (EventException e) {
            mSuccess = false;
            throw e;
        } catch (Exception e) {
            mSuccess = false;
            throw new EventException(e);
        }
        
        mSuccess = true;
    }
    
    public abstract void protectedExecute(Entity e, SimulationProxy p)throws Exception;
    
}
