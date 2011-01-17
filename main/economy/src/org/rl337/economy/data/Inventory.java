package org.rl337.economy.data;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Resource, Inventory.InventoryItem> mInventory;
    
    public Inventory() {
        mInventory = new HashMap<Resource, Inventory.InventoryItem>();
    }
    
    public void give(Resource r, int qty) {
        if (qty < 1) {
            return;
        }
        
        if (!mInventory.containsKey(r)) {
            mInventory.put(r, new Inventory.InventoryItem(r, qty));
        } else {
            mInventory.get(r).increment(qty);
        }
    }
    
    public InventoryItem take(Resource r, int qty) {
        if (!has(r, qty)) {
            return null;
        }
        
        InventoryItem item = mInventory.get(r);
        item.decrement(qty);
        
        return new InventoryItem(r, qty);
    }
    
    public boolean has(Resource r, int qty) {
        return qty > 0 && mInventory.containsKey(r) && mInventory.get(r).getQuantity() >= qty;
    }
    
    public int amount(Resource r) {
        if (!mInventory.containsKey(r)) {
            return 0;
        }
        
        return mInventory.get(r).getQuantity();
    }

    static class InventoryItem {
        private int mQuantity;
        private Resource mType;
        
        public InventoryItem(Resource rType, int qty) {
            mType = rType;
            mQuantity = qty;
        }
        
        public Resource getType() {
            return mType;
        }
        
        public int getQuantity() {
            return mQuantity;
        }
        
        public void decrement(int qty) {
            mQuantity -= qty;
        }
        
        public void increment(int qty) {
            mQuantity += qty;
        }
        
        public void setQuantity(int qty) {
            mQuantity = qty;
        }
    }

}
