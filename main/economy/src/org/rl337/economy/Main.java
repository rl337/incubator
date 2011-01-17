package org.rl337.economy;

import java.util.logging.Logger;

import org.rl337.economy.data.Entity;
import org.rl337.economy.data.Event;

public class Main {
    private static final Logger smLogger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        smLogger.info("Starting simulation");
        
        long maxTicks = 5000;
        int entityCount = 100;
        
        Entity[] entities = new Entity[entityCount];
        for(int i = 0; i < entities.length; i++) {
            entities[i] = new Entity(Integer.toString(i));
        }
        
        smLogger.info("Simulating " + entityCount + " entities for " + maxTicks + " ticks");

        EventLoop eventLoop = new EventLoop();
        for(long i = 0; i < maxTicks; i++) {
            for(Entity e : entities) {
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
        
        for(int i = 0; i < entities.length; i++) {
            int happiness = entities[i].getHappiness();
            int bucket = (happiness * buckets.length) / 1024;
            if (bucket < 0) { bucket = 0; }
            if (bucket >= buckets.length) { bucket = buckets.length - 1; }
            
            buckets[bucket]++;
            
        }
        
        smLogger.info("Stopped simulation");
        
        for(int i = 0; i < buckets.length; i++) {
            String result = String.format("%05d ", i);
            for(int j = 0; j < (buckets[i] * 50) / entities.length; j++) {
                result += "*";
            }
            System.out.println(result); 
        }
    }
    

}
