package org.rl337.economy.data;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Inventory {
    @Expose @SerializedName("inv")
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

    public static class InventoryItem {
        @Expose @SerializedName("qty")
        private int mQuantity;
        @Expose @SerializedName("type")
        private Resource mType;
        
        public InventoryItem() {
            mType = Resource.Unknown;
            mQuantity = 0;
        }
        
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
