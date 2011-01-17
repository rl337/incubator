package org.rl337.economy.data;

public interface EventLoopProxy {
    boolean addEvent(Event e);

    long getCurrentTick();
}
