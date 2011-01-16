package org.rl337.economy.data;

public abstract class AbstractEvent implements Event {
    private long mExecuteOnTick;
    private long mExecutedTick;
    private boolean mSuccess;
    
    public AbstractEvent(long tickToExecute) {
        mExecuteOnTick = tickToExecute;
        mExecutedTick = -1;
        mSuccess = false;
    }

    @Override
    public long getExecuteOnTick() {
        return mExecuteOnTick;
    }
    
    public long getExecutedOnTick() {
        return mExecutedTick;
    }
    
    public boolean success() {
        return mSuccess;
    }
    
    public boolean executed() {
        return mExecutedTick != -1;
    }
    
    public void execute(EventLoopProxy p) throws EventException {
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
    
    public abstract void protectedExecute(EventLoopProxy p)throws Exception;
    
}
