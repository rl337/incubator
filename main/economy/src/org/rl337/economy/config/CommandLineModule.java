package org.rl337.economy.config;

import org.rl337.economy.EntityFactory;
import org.rl337.economy.KeyFactory;
import org.rl337.economy.Simulation;
import org.rl337.economy.SimulationProxy;

import com.google.inject.AbstractModule;

public class CommandLineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(KeyFactory.class).toInstance(new KeyFactory());
        bind(EntityFactory.class).toInstance(new EntityFactory());
        bind(Simulation.class).asEagerSingleton();
        bind(SimulationProxy.class).to(Simulation.class);
    }

}
