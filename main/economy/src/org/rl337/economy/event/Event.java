package org.rl337.economy.event;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.Tick;

public interface Event {

    Tick getExecuteOnTick();

    void execute(SimulationProxy proxy) throws EventException;

    public static class EventException extends Exception {
        private static final long serialVersionUID = 3074705375283037439L;

        public EventException(String reason) {
            super(reason);
        }

        public EventException(Exception e) {
            super(e);
        }
    }

}
