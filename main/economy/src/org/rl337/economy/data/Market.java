package org.rl337.economy.data;

import java.util.Comparator;
import java.util.TreeSet;

import org.rl337.economy.KeyFactory;
import org.rl337.economy.KeyFactory.Key;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.data.entity.MarketUser;

public class Market {
    private TreeSet<Bid> mOfferExpirations;
    private TreeSet<Bid> mBuyExpirations;
    
    private TreeSet<Bid> mOffers;
    private TreeSet<Bid> mBuys;
    
    private KeyFactory mKeyFactory;
    
    public Market(KeyFactory keyFactory) {
        mOfferExpirations = new TreeSet<Bid>(new CompareByCost());
        mBuyExpirations = new TreeSet<Bid>(new CompareByCost());
        mKeyFactory = keyFactory;
        
        mOffers = new TreeSet<Bid>(new CompareByExpirationTick());
        mBuys = new TreeSet<Bid>(new CompareByExpirationTick());
    }
    
    public void offer(MarketUser user, Resource resource, int qty, int cost, long exp) {
        Bid offer = newBid(user, resource, qty, cost, exp);
        mOfferExpirations.add(offer);
        mOffers.add(offer);
    }
    
    public void buy(MarketUser user, Resource resource, int qty, int cost, long exp) {
        Bid buy = newBid(user, resource, qty, cost, exp);
        mBuys.add(buy);
        mBuyExpirations.add(buy);
    }
    
    public void executeTick(Key tick) {
        long tickValue = tick.getValue();
        // First expire any offers or buys that are set to expire before this tick.
        while(!mBuyExpirations.isEmpty() && mBuyExpirations.first().getExpiration() < tickValue) {
            Bid buy = mBuyExpirations.pollFirst();
            buy.getMarketUser().onBuyExpired(buy);
            mBuys.remove(buy);
        }

        while(!mOfferExpirations.isEmpty() && mOfferExpirations.first().getExpiration() < tickValue) {
            Bid offer = mOfferExpirations.pollFirst();
            offer.getMarketUser().onOfferExpired(offer);
            mOffers.remove(offer);
        }
        
        satisfyBuys();
        satisfyOffers();
    }
    
    private Bid newBid(MarketUser user, Resource resource, int qty, int cost, long exp) {
        Key key = mKeyFactory.newKey(KeyType.Bid);
        return new Bid(key, user, resource, qty, cost, exp);
    }

    
    private void satisfyOffers() {
        Bid[] sells = mOffers.toArray(new Bid[mOffers.size()]);
        
        for(Bid sell : sells) {
            while (!mBuys.isEmpty() && sell.getQuantityLeft() > 0) {
                // get the lowest offer
                Bid buy = mBuys.last();
                int buyCost = buy.getCost();
                
                // if the highest buy is less than our sales price, exit
                if (buyCost < sell.getCost()) {
                    break;
                }
                
                
                int buyQty = buy.getQuantityLeft();
                int inventory = sell.getQuantityLeft();
                // If the buy needs more than what we have, execute partial on the buy
                // then break
                if (inventory < buyQty) {
                    buy.take(inventory);
                    sell.take(inventory);
                    sell.getMarketUser().onOfferExecuted(sell, buy, inventory);
                    buy.getMarketUser().onBuyExecuted(sell, buy, inventory);
                    break;
                } 
    
                // If we get here, we have more or exactly the same as the buy
                sell.take(buyQty);
                buy.take(buyQty);
                sell.getMarketUser().onOfferExecuted(sell, buy, buyQty);
                buy.getMarketUser().onBuyExecuted(sell, buy, buyQty);
                mBuys.remove(buy);
            }
            
            if (sell.getQuantityLeft() < 1) {
                mOffers.remove(sell);
            }
        }
    }

    
    private void satisfyBuys() {
        Bid[] buys = mBuys.toArray(new Bid[mBuys.size()]);
        for(Bid buy : buys) {
            while (!mOffers.isEmpty() && buy.getQuantityLeft() > 0) {
                // get the lowest offer
                Bid offer = mOffers.first();
                int offerCost = offer.getCost();
                
                // if the lowest offer is more than the bid, we stop.
                if (offerCost > buy.getCost()) {
                    break;
                }
                
                int offerQty = offer.getQuantityLeft();
                int needed = buy.getQuantityLeft();
                // If the offer has more than we need, we need
                // to execute it as a partial offer execution
                if (needed < offerQty) {
                    offer.take(needed);
                    buy.take(needed);
                    offer.getMarketUser().onOfferExecuted(offer, buy, needed);
                    buy.getMarketUser().onBuyExecuted(offer, buy, needed);
                    break;
                } 
    
                // if we get here, the offer exactly satisfies or we need
                // more than the offer has.
                offer.take(offerQty);
                buy.take(offerQty);
                offer.getMarketUser().onOfferExecuted(offer, buy, offerQty);
                buy.getMarketUser().onBuyExecuted(offer, buy, offerQty);
                mOffers.remove(offer);
            }
            
            if (buy.getQuantityLeft() < 1) {
                mBuys.remove(buy);
            }
        }
    }
    
    public Bid[] getActiveBuys() {
        return mBuys.toArray(new Bid[mBuys.size()]);
    }
    
    public Bid[] getActiveOffers() {
        return mOffers.toArray(new Bid[mOffers.size()]);
    }

    public static class Bid {
        private Key mId;
        private MarketUser mMarketUser;
        private Resource mResource;
        private int mQuantity;
        private int mQtyLeft;
        private int mCost;
        private long mExpiration;
        
        private Bid(Key key, MarketUser e, Resource r, int q, int c, long expiresOn) {
            mId = key;
            mMarketUser = e;
            mResource = r;
            mQuantity = q;
            mCost = c;
            mExpiration = expiresOn;
            mQtyLeft = q;
        }
        
        public Key getKey() {
            return mId;
        }
        
        public MarketUser getMarketUser() {
            return mMarketUser;
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
        
        public int getQuantityLeft() {
            return mQtyLeft;
        }
        
        public boolean take(int qty) {
            if (qty > mQtyLeft) {
                return false;
            }
            
            mQtyLeft -= qty;
            return true;
        }
        
        public int getCost() {
            return mCost;
        }
    }
    
    private static class CompareByExpirationTick implements Comparator<Bid> {

        @Override
        public int compare(Bid arg0, Bid arg1) {
            long exp0 = arg0.getExpiration();
            long exp1 = arg1.getExpiration();
            
            if (exp0 == exp1) {
                long id0 = arg0.getKey().getValue();
                long id1 = arg1.getKey().getValue();
                if (id0 == id1) {
                    return 0;
                }
                
                if (id0 < id1) {
                    return -1;
                }
                
                return 1;
            }
            
            if (exp0 < exp1) { return -1; }
            return 1;
        }
    }

    private static class CompareByCost implements Comparator<Bid> {
        @Override
        public int compare(Bid arg0, Bid arg1) {
            int result = arg0.getCost() - arg1.getCost();
            if (result != 0) return result;
            
            long id0 = arg0.getKey().getValue();
            long id1 = arg1.getKey().getValue();
            if (id0 == id1) {
                return 0;
            }
            
            if (id0 < id1) {
                return -1;
            }
            
            return 1;
        }
    }

}
