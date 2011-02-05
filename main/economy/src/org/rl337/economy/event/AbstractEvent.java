package org.rl337.economy.event;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.Tick;

public abstract class AbstractEvent implements Event {
    private Tick mExecuteOnTick;
    private Tick mExecutedTick;
    private boolean mSuccess;
    
    public AbstractEvent(Tick tickToExecute) {
        mExecuteOnTick = tickToExecute;
        mExecutedTick = null;
        mSuccess = false;
    }

    @Override
    public Tick getExecuteOnTick() {
        return mExecuteOnTick;
    }
    
    public Tick getExecutedOnTick() {
        return mExecutedTick;
    }
    
    public boolean success() {
        return mSuccess;
    }
    
    public boolean executed() {
        return mExecutedTick != null;
    }
    
    public void execute(SimulationProxy p) throws EventException {
        mExecutedTick = p.getCurrentTick();
        
        try {
            protectedExecute(p);
        } catch (EventException e) {
            throw e;
        } catch (Exception e) {
            throw new EventException(e);
        }
        
        mSuccess = true;
    }
    
    public abstract void protectedExecute(SimulationProxy p)throws Exception;
    
}
