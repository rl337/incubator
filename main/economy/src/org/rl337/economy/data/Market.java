package org.rl337.economy.data;

import java.util.Comparator;
import java.util.TreeSet;

import org.rl337.economy.data.entity.MarketUser;

public class Market {
    private TreeSet<Bid> mOfferExpirations;
    private TreeSet<Bid> mBuyExpirations;
    
    private TreeSet<Bid> mOffers;
    private TreeSet<Bid> mBuys;
    
    public Market() {
        mOfferExpirations = new TreeSet<Bid>(new CompareByCost());
        mBuyExpirations = new TreeSet<Bid>(new CompareByCost());
        
        mOffers = new TreeSet<Bid>(new CompareByExpirationTick());
        mBuys = new TreeSet<Bid>(new CompareByExpirationTick());
    }
    
    public void offer(MarketUser offerer, Bid offer) {
        mOfferExpirations.add(offer);
        mOffers.add(offer);
    }
    
    public void buy(MarketUser buyer, Bid buy) {
        mBuys.add(buy);
        mBuyExpirations.add(buy);
    }
    
    public void executeTick(long tick) {
        // First expire any offers or buys that are set to expire before this tick.
        while(mBuyExpirations.first().getExpiration() < tick) {
            Bid buy = mBuyExpirations.pollFirst();
            buy.getMarketUser().onBuyExpired(buy);
            mBuys.remove(buy);
        }
        
        while(mOfferExpirations.first().getExpiration() < tick) {
            Bid offer = mOfferExpirations.pollFirst();
            offer.getMarketUser().onOfferExpired(offer);
            mOffers.remove(offer);
        }
    }
    
    public Bid[] getActiveBuys() {
        return mBuys.toArray(new Bid[mBuys.size()]);
    }
    
    public Bid[] getActiveOffers() {
        return mOffers.toArray(new Bid[mOffers.size()]);
    }

    public static class Bid {
        private MarketUser mMarketUser;
        private Resource mResource;
        private int mQuantity;
        private int mCost;
        private long mExpiration;
        
        public Bid(MarketUser e, Resource r, int q, int c, long expiresOn) {
            mMarketUser = e;
            mResource = r;
            mQuantity = q;
            mCost = c;
            mExpiration = expiresOn;
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
            
            if (exp0 == exp1) { return 0; }
            if (exp0 < exp1) { return -1; }
            return 1;
        }
    }

    private static class CompareByCost implements Comparator<Bid> {
        @Override
        public int compare(Bid arg0, Bid arg1) {
            return arg0.getCost() - arg1.getCost();
        }
    }

}
