package org.rl337.economy.data;

import java.util.HashMap;
import java.util.LinkedList;

import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.data.entity.MarketUser;

public class Market {
    private HashMap<Long, Bid> mOfferExpirations;
    private HashMap<Long, Bid> mBuyExpirations;
    
    private LinkedList<Bid> mOffers;
    private LinkedList<Bid> mBuys;
    
    
    public Market() {
        mOfferExpirations = new HashMap<Long, Bid>();
        mBuyExpirations = new HashMap<Long, Bid>();
        
        mOffers = new LinkedList<Bid>();
        mBuys = new LinkedList<Bid>();
    }
    
    public void offer(MarketUser offerer, Bid offer) {
        // First we see if there are any buys queued that could execute.
        
        
        // If not, or we don't have a complete transaction, we queue it.
    }
    
    public void buy(MarketUser buyer, Bid buy) {
        
    }

    public static class Bid {
        private Entity mEntity;
        private Resource mResource;
        private int mQuantity;
        private int mCost;
        private long mExpiration;
        
        public Bid(Entity e, Resource r, int q, int c, long expiresOn) {
            mEntity = e;
            mResource = r;
            mQuantity = q;
            mCost = c;
            mExpiration = expiresOn;
        }
        
        public Entity getEntity() {
            return mEntity;
        }
        
        public long getExpiration() {
            return mExpiration;
        }
        
        public Resource getResource() {
            return mResource;
        }
        
        public int getQuantity() {
            return mQuantity;
        }
        
        public boolean take(int qty) {
            if (qty < mQuantity) {
                return false;
            }
            
            mQuantity -= qty;
            return true;
        }
        
        public int getCost() {
            return mCost;
        }
    }

}
