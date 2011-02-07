package org.rl337.economy.data.entity;

import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Resource;
import org.rl337.economy.data.Inventory.InventoryItem;
import org.rl337.economy.event.Event;

public interface Entity {

    EntityKey getKey();
    void setKey(EntityKey key);

    String getName();
    void setName(String name);
    
    Tick getBornOnTick();
    void setBornOnTick(Tick tick);

    void give(Resource type, int qty);

    InventoryItem take(Resource type, int qty);

    int getHappiness();
    void setHappiness(int happiness);
    void makeHappy(int qty);
    void makeSad(int qty);

    int getCredits();
    void setCredits(int credits);
    void credit(int qty);
    void debit(int qty);

    boolean isAlive();

    void die();

    Event getEvent(Tick tick);

}
