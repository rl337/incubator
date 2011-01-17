package org.rl337.economy.data;

import java.util.Random;
import java.util.logging.Logger;

import org.rl337.economy.data.Inventory.InventoryItem;


public class Entity {
    private static final Logger smLogger = Logger.getLogger(Entity.class.getName());
    private static final Random smRandom = new Random();

    private String mName;
    private int mHappiness;
    private int mCredits;
    private Inventory mInventory;

    public Entity(String name) {
        mName = name;
        mInventory = new Inventory();
    }
    
    public String getName() {
        return mName;
    }
    
    public void give(Resource type, int qty) {
        mInventory.give(type, qty);
    }
    
    public InventoryItem take(Resource type, int qty) {
        return mInventory.take(type, qty);
    }
    
    public int getHappiness() {
        return mHappiness;
    }
    
    public int getCredits() {
        return mCredits;
    }
    
    public void setCredits(int credits) {
        mCredits = credits;
    }
    
    public void setHappiness(int happiness) {
        mHappiness = happiness;
    }
    
    public void credit(int qty) {
        mCredits += qty;
    }
    
    public void debit(int qty) {
        mCredits -= qty;
    }
    
    public void makeHappy(int qty) {
        mHappiness += qty;
    }
    
    public void makeSad(int qty) {
        mHappiness -= qty;
    }
    
    public Event getEvent(long tick) {
        // If we're really happy, we are gonna just do nothing but become just slightly less happy.
        if (mHappiness > 1024) {
            return new AbstractEvent(tick) {
                @Override
                public void protectedExecute(EventLoopProxy p) throws Exception { 
                    
                    smLogger.info(mName + "is happy.");
                    mHappiness--;
                }
            };
        }
        
        // If we have less than 1024 happiness, we want to eat something... but if we have no food
        // in inventory, we want to convert Perishables to Food... and if we have no perishables
        // we want to forage.. which gives us perishables.
        return new AbstractEvent(tick) {
            public void protectedExecute(EventLoopProxy p) throws Exception {
                if (mInventory.has(Resource.Food, 1)) {
                    InventoryItem food = mInventory.take(Resource.Food, 1);
                    if (food != null) {
                        smLogger.info(mName + " eats a food");
                        mHappiness += 16; // eating gives us 16 happiness.
                    }
                } else {
                    // we had no food, let's convert perishables to food
                    // it takes 4 perishables to make 1 food.
                    InventoryItem perishable = mInventory.take(Resource.Perishables, 4);
                    if (perishable != null) {
                        smLogger.info(mName + " converted Perishables into food.");
                        mInventory.give(Resource.Food, 1);
                    } else {
                        // We had no food and we don't have enough perishables... we have to forage.
                        // When foraging we have a 1/3 chance of finding Perishables
                        if (smRandom.nextInt() % 4 == 0) { 
                            smLogger.info(mName + " foraged and found a perishable");
                            mInventory.give(Resource.Perishables, 1);
                        } else {
                            smLogger.info(mName + " foraged but found nothing.");
                        }
                    }
                }
                mHappiness--;
            }
        };
    }

}