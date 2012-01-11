package org.rl337.economy;

import java.util.List;

import org.rl337.economy.config.CommandLineModule;
import org.rl337.economy.data.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    private static final Logger smLogger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new CommandLineModule());

        smLogger.info("Starting simulation");
        
        long maxTicks = 100000;
        int entityCount = 1;
        
        Simulation sim = injector.getInstance(Simulation.class);

        for(int i = 0; i < entityCount; i++) {
            sim.addEntity(Integer.toString(i));
        }
        
        smLogger.info("Simulating " + entityCount + " entities for " + maxTicks + " ticks");

        for(long i = 0; i < maxTicks; i++) {
            if (sim.entityCount() < 1) {
                break;
            }
            
            sim.executeTick();
        }

        
        int[] buckets = new int[20];
        for(int i = 0; i < buckets.length; i++) {
            buckets[i] = 0;
        }
        
        List<Entity> entities = sim.listEntities();
        for(int i = 0; i < entities.size(); i++) {
            int happiness = entities.get(i).getHappiness();
            int bucket = (happiness * buckets.length) / 1024;
            if (bucket < 0) { bucket = 0; }
            if (bucket >= buckets.length) { bucket = buckets.length - 1; }
            
            buckets[bucket]++;
            
        }
        
        smLogger.info("Stopped simulation");
        int endCount = entities.size() > 0 ? entities.size() : 1;
        for(int i = 0; i < buckets.length; i++) {
            String result = String.format("%05d ", i);
            for(int j = 0; j < (buckets[i] * 50) / endCount; j++) {
                result += "*";
            }
            System.out.println(result); 
        }
    }
}
