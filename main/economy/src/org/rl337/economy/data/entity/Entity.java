package org.rl337.economy.data.entity;

import java.util.Random;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Inventory;
import org.rl337.economy.data.Resource;
import org.rl337.economy.data.Inventory.InventoryItem;
import org.rl337.economy.event.AbstractEvent;
import org.rl337.economy.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entity {
    private static final Logger smLogger = LoggerFactory.getLogger(Entity.class);
    private static final Random smRandom = new Random();

    @Expose @SerializedName("key")
    private EntityKey mKey;
    @Expose @SerializedName("name")
    private String mName;
    @Expose @SerializedName("happiness")
    private int mHappiness;
    @Expose @SerializedName("credits")
    private int mCredits;
    @Expose @SerializedName("inventory")
    private Inventory mInventory;
    @Expose @SerializedName("born")
    private Tick mBornTick;
    @Expose @SerializedName("alive")
    private boolean mAlive;
    
    public Entity() {
        mInventory = new Inventory();
        mHappiness = 300;
        mCredits = 0;
        mAlive = true;
    }

    public Entity(EntityKey key, String name, Tick tick) {
        this();
        mKey = key;
        mName = name;
        mBornTick = tick;
    }
    
    public EntityKey getKey() {
        return mKey;
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
    
    public boolean isAlive() {
        return mAlive;
    }
    
    public void die() {
        mAlive = false;
    }
    
    public Event getEvent(Tick tick) {

        // once we reach 10000 we remove ourselves
        if (mAlive && tick.getValue() - mBornTick.getValue() > 10000) {
            die();
        }
        
        // If we're really happy, we are gonna just do nothing but become just slightly less happy.
        if (mHappiness > 1024) {
            return new AbstractEvent(tick) {
                @Override
                public void protectedExecute(SimulationProxy p) throws Exception { 
                    
                    smLogger.debug(mName + " is happy.");
                    
                    if (smRandom.nextInt() % 5 == 0) {
                        smLogger.debug(mName + " decided to divide");
                        mHappiness = mHappiness / 4;
                        p.addEntity(mName + "-" + Long.toString(p.getCurrentTick().getValue()));
                    }
                    mHappiness--;
                }
            };
        }
        
        // If we have less than 1024 happiness, we want to eat something... but if we have no food
        // in inventory, we want to convert Perishables to Food... and if we have no perishables
        // we want to forage.. which gives us perishables.
        return new AbstractEvent(tick) {
            public void protectedExecute(SimulationProxy p) throws Exception {
                if (mInventory.has(Resource.Food, 1)) {
                    InventoryItem food = mInventory.take(Resource.Food, 1);
                    if (food != null) {
                        smLogger.debug(mName + " eats a food");
                        mHappiness += 16; // eating gives us 16 happiness.
                    }
                } else {
                    // we had no food, let's convert perishables to food
                    // it takes 4 perishables to make 1 food.
                    InventoryItem perishable = mInventory.take(Resource.Perishables, 4);
                    if (perishable != null) {
                        smLogger.debug(mName + " converted Perishables into food.");
                        mInventory.give(Resource.Food, 1);
                    } else {
                        // We had no food and we don't have enough perishables... we have to forage.
                        // When foraging we have a 1/3 chance of finding Perishables
                        if (smRandom.nextInt() % 3 == 0) { 
                            smLogger.debug(mName + " foraged and found a perishable");
                            mInventory.give(Resource.Perishables, 1);
                        } else {
                            smLogger.debug(mName + " foraged but found nothing.");
                        }
                    }
                }
                mHappiness--;
            }
        };
    }

}
