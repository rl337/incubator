package org.rl337.economy.data;

import java.util.Comparator;
import java.util.TreeSet;

import org.rl337.economy.data.entity.MarketUser;

public class Market {
    private TreeSet<Bid> mOfferExpirations;
    private TreeSet<Bid> mBuyExpirations;
    
    private TreeSet<Bid> mOffers;
    private TreeSet<Bid> mBuys;
    
    private long mCurrentId;
    
    private long nextId() {
        return mCurrentId++;
    }
    
    public Market() {
        mOfferExpirations = new TreeSet<Bid>(new CompareByCost());
        mBuyExpirations = new TreeSet<Bid>(new CompareByCost());
        
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
    
    public void executeTick(long tick) {
        // First expire any offers or buys that are set to expire before this tick.
        while(!mBuyExpirations.isEmpty() && mBuyExpirations.first().getExpiration() < tick) {
            Bid buy = mBuyExpirations.pollFirst();
            buy.getMarketUser().onBuyExpired(buy);
            mBuys.remove(buy);
        }

        while(!mOfferExpirations.isEmpty() && mOfferExpirations.first().getExpiration() < tick) {
            Bid offer = mOfferExpirations.pollFirst();
            offer.getMarketUser().onOfferExpired(offer);
            mOffers.remove(offer);
        }
        
    }
    
    private Bid newBid(MarketUser user, Resource resource, int qty, int cost, long exp) {
        return new Bid(nextId(), user, resource, qty, cost, exp);
    }
    
    private Bid[] satisfyBid(Bid bid, TreeSet<Bid> pool) {
        
        //for()
        
        
        return null;
    }
    
    public Bid[] getActiveBuys() {
        return mBuys.toArray(new Bid[mBuys.size()]);
    }
    
    public Bid[] getActiveOffers() {
        return mOffers.toArray(new Bid[mOffers.size()]);
    }

    public static class Bid {
        private long mId;
        private MarketUser mMarketUser;
        private Resource mResource;
        private int mQuantity;
        private int mCost;
        private long mExpiration;
        
        private Bid(long id, MarketUser e, Resource r, int q, int c, long expiresOn) {
            mId = id;
            mMarketUser = e;
            mResource = r;
            mQuantity = q;
            mCost = c;
            mExpiration = expiresOn;
        }
        
        public long getId() {
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
    
    private static class CompareByExpirationTick implements Comparator<Bid> {

        @Override
        public int compare(Bid arg0, Bid arg1) {
            long exp0 = arg0.getExpiration();
            long exp1 = arg1.getExpiration();
            
            if (exp0 == exp1) {
                long id0 = arg0.getId();
                long id1 = arg1.getId();
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
            
            long id0 = arg0.getId();
            long id1 = arg1.getId();
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
