package org.rl337.economy.data;

public interface Event {

	long getExecutionTick();

	void execute(EventLoopProxy proxy);

}
