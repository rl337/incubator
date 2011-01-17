package org.rl337.economy.data;

public interface Event {

    long getExecuteOnTick();

    void execute(EventLoopProxy proxy) throws EventException;

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
