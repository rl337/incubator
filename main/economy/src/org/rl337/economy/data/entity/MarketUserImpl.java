package org.rl337.economy.data.entity;

import java.util.Random;

import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Inventory;
import org.rl337.economy.data.Resource;
import org.rl337.economy.data.Inventory.InventoryItem;
import org.rl337.economy.data.Market.Bid;
import org.rl337.economy.event.AbstractEvent;
import org.rl337.economy.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MarketUserImpl implements MarketUser {
    private static final Logger smLogger = LoggerFactory.getLogger(MarketUserImpl.class);
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
    
    public MarketUserImpl() {
        mInventory = new Inventory();
        mHappiness = 300;
        mCredits = 0;
        mAlive = true;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#getKey()
     */
    public EntityKey getKey() {
        return mKey;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#getName()
     */
    public String getName() {
        return mName;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#give(org.rl337.economy.data.Resource, int)
     */
    public void give(Resource type, int qty) {
        mInventory.give(type, qty);
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#take(org.rl337.economy.data.Resource, int)
     */
    public InventoryItem take(Resource type, int qty) {
        return mInventory.take(type, qty);
    }
    
    public boolean has(Resource type, int qty) {
        return mInventory.has(type, qty);
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#getHappiness()
     */
    public int getHappiness() {
        return mHappiness;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#getCredits()
     */
    public int getCredits() {
        return mCredits;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#setCredits(int)
     */
    public void setCredits(int credits) {
        mCredits = credits;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#setHappiness(int)
     */
    public void setHappiness(int happiness) {
        mHappiness = happiness;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#credit(int)
     */
    public void credit(int qty) {
        mCredits += qty;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#debit(int)
     */
    public void debit(int qty) {
        mCredits -= qty;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#makeHappy(int)
     */
    public void makeHappy(int qty) {
        mHappiness += qty;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#makeSad(int)
     */
    public void makeSad(int qty) {
        mHappiness -= qty;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#isAlive()
     */
    public boolean isAlive() {
        return mAlive;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#die()
     */
    public void die() {
        mAlive = false;
    }
    
    /* (non-Javadoc)
     * @see org.rl337.economy.data.entity.Entity#getEvent(org.rl337.economy.KeyFactory.Tick)
     */
    @SuppressWarnings("serial")
    public Event getEvent(Tick tick) {

        // once we reach 10000 we remove ourselves
        if (mAlive && tick.getValue() - mBornTick.getValue() > 10000) {
            die();
            return null;
        }
        
        Tick nextTick = tick.getFutureTick(1);
        
        // If we're really happy, we are gonna just do nothing but become just slightly less happy.
        if (mHappiness > 1024) {
            return new AbstractEvent(mKey, nextTick) {
                @Override
                public void protectedExecute(Entity e, SimulationProxy p) throws Exception { 
                    
                    smLogger.debug(mName + " is happy.");
                    
                    if (smRandom.nextInt() % 5 == 0) {
                        smLogger.debug(e.getName() + " decided to divide");
                        e.setHappiness(e.getHappiness() / 4);
                        p.addEntity(e.getName() + "-" + Long.toString(p.getCurrentTick().getValue()));
                    }
                    e.makeSad(1);
                }
            };
        }
        
        // If we have less than 1024 happiness, we want to eat something... but if we have no food
        // in inventory, we want to convert Perishables to Food... and if we have no perishables
        // we want to forage.. which gives us perishables.
        return new AbstractEvent(mKey, nextTick) {
            public void protectedExecute(Entity e, SimulationProxy p) throws Exception {
                if (e.has(Resource.Food, 1)) {
                    InventoryItem food = e.take(Resource.Food, 1);
                    if (food != null) {
                        smLogger.debug(mName + " eats a food");
                        e.makeHappy(16); // eating gives us 16 happiness.
                    }
                } else {
                    // we had no food, let's convert perishables to food
                    // it takes 4 perishables to make 1 food.
                    InventoryItem perishable = e.take(Resource.Perishables, 4);
                    if (perishable != null) {
                        smLogger.debug(mName + " converted Perishables into food.");
                        e.give(Resource.Food, 1);
                    } else {
                        // We had no food and we don't have enough perishables... we have to forage.
                        // When foraging we have a 1/3 chance of finding Perishables
                        if (smRandom.nextInt() % 3 == 0) { 
                            smLogger.debug(mName + " foraged and found a perishable");
                            e.give(Resource.Perishables, 1);
                        } else {
                            smLogger.debug(mName + " foraged but found nothing.");
                        }
                    }
                }
                e.makeSad(1);
            }
        };
    }

    @Override
    public void onBuyExecuted(Bid offer, Bid request, int executedQty) {
    }

    @Override
    public void onBuyExpired(Bid buy) {
    }

    @Override
    public void onOfferExecuted(Bid offer, Bid request, int executedQty) {
    }

    @Override
    public void onOfferExpired(Bid offer) {
    }

    @Override
    public Tick getBornOnTick() {
        return mBornTick;
    }
    
    public void setBornOnTick(Tick tick) {
        mBornTick = tick;
    }

    @Override
    public void setKey(EntityKey key) {
        mKey = key;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

}
