package org.rl337.economy;

import java.util.List;
import java.util.logging.Logger;

import org.rl337.economy.data.Event;
import org.rl337.economy.data.Simulation;
import org.rl337.economy.data.entity.Entity;

public class Main {
    private static final Logger smLogger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        smLogger.info("Starting simulation");
        
        long maxTicks = 100000;
        int entityCount = 1;
        
        Simulation sim = new Simulation();
        for(int i = 0; i < entityCount; i++) {
            sim.addEntity(new Entity(Integer.toString(i), sim, 0));
        }
        
        smLogger.info("Simulating " + entityCount + " entities for " + maxTicks + " ticks");

        EventLoop eventLoop = new EventLoop();
        for(long i = 0; i < maxTicks; i++) {
            
            if (sim.entityCount() < 1) {
                break;
            }
            
            for(Entity e : sim.getEntities()) {
                Event event = e.getEvent(i + 1);
                if (event != null) {
                    eventLoop.addEvent(event);
                }
            }
            
            int executed = eventLoop.executeNextTick();
            smLogger.info("Executed " + executed + " during tick " + i);
        }

        
        int[] buckets = new int[20];
        for(int i = 0; i < buckets.length; i++) {
            buckets[i] = 0;
        }
        
        List<Entity> entities = sim.getEntities();
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
