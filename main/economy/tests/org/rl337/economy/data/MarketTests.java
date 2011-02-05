package org.rl337.economy.data;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rl337.economy.KeyFactory;
import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.Market.Bid;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.data.entity.MarketUser;

public class MarketTests extends TestCase {
    private Market mMarket;
    private TestMarketUser mSeller;
    private TestMarketUser mBuyer;
    private KeyFactory mFactory;
    
    public void setUp() {
        mFactory = new KeyFactory();
        mMarket = new Market();
        mMarket.initialize(mFactory);
        
        mBuyer = new TestMarketUser(
            (EntityKey) mFactory.newKey(KeyType.Entity), 
            "Buyer", 
            (Tick) mFactory.currentKey(KeyType.Tick)
        );
        
        mSeller = new TestMarketUser(
            (EntityKey) mFactory.newKey(KeyType.Entity),
            "Seller",
            (Tick) mFactory.currentKey(KeyType.Tick)
        );
    }
    
    public void testSimpleOfferAndBuy() {
        mMarket.offer(mSeller, Resource.Food, 10, 250, 50);
        mMarket.buy(mBuyer, Resource.Food, 10, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        assertEquals("market should have no more active buys", 0, mMarket.getActiveBuys().length);
        assertEquals("market should have no more active sells", 0, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, buy.buy.getMarketUser());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have nothing in QtyLeft", 0, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller, buy.offer.getMarketUser());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, sell.buy.getMarketUser());
        assertEquals("Seller in buy exeuction should be seller", mSeller, sell.offer.getMarketUser());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have nothing in QtyLeft", 0, sell.offer.getQuantityLeft());
    }

    public void testPartialOffer() {
        mMarket.offer(mSeller, Resource.Food, 50, 250, 50);
        mMarket.buy(mBuyer, Resource.Food, 10, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        assertEquals("market should have no more active buys", 0, mMarket.getActiveBuys().length);
        assertEquals("market should still have one active sell", 1, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, buy.buy.getMarketUser());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have nothing in QtyLeft", 0, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller, buy.offer.getMarketUser());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, sell.buy.getMarketUser());
        assertEquals("Seller in buy exeuction should be seller", mSeller, sell.offer.getMarketUser());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have 40 QtyLeft", 40, sell.offer.getQuantityLeft());
    }

    public void testPartialBuy() {
        mMarket.offer(mSeller, Resource.Food, 10, 250, 50);
        mMarket.buy(mBuyer, Resource.Food, 50, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        assertEquals("market should still have one active buys", 1, mMarket.getActiveBuys().length);
        assertEquals("market should have no active sells", 0, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, buy.buy.getMarketUser());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have 40 left in QtyLeft", 40, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller, buy.offer.getMarketUser());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer, sell.buy.getMarketUser());
        assertEquals("Seller in buy exeuction should be seller", mSeller, sell.offer.getMarketUser());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have 0 QtyLeft", 0, sell.offer.getQuantityLeft());
    }
    
    public void testMultipleIdenticalOffers() {
        mMarket.offer(mSeller, Resource.Food, 1, 10, 3);
        mMarket.offer(mSeller, Resource.Food, 1, 10, 3);
        
        assertEquals("Seller should have 2 identical offers", 2, mMarket.getActiveOffers().length);
        
        mMarket.buy(mBuyer, Resource.Food, 1, 10, 10);
        mMarket.buy(mBuyer, Resource.Food, 1, 10, 10);
        assertEquals("Buyer should have 2 identical buys", 2, mMarket.getActiveBuys().length);
    }
    
    public void testOfferAndBuyExpiration() {
        mMarket.offer(mSeller, Resource.Food, 1, 10, 3);
        mMarket.offer(mSeller, Resource.Food, 1, 10, 50);

        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        
        assertEquals("Seller should have had 1 expired sell", 1, mSeller.getgetExpiredSells().size());
        assertEquals("Seller should have had no expired buys", 0, mSeller.getExpiredBuys().size());
        assertEquals("Seller should have had no buys", 0, mSeller.getBuys().size());
        assertEquals("Seller should have had no sells", 0, mSeller.getSells().size());
        
        mMarket.buy(mBuyer, Resource.Food, 1, 5, 10);

        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mFactory.newKey(KeyType.Tick));

        assertEquals("Buyer should have had no expired sells", 0, mBuyer.getgetExpiredSells().size());
        assertEquals("Buyer should have had one expired buy", 1, mBuyer.getExpiredBuys().size());
        assertEquals("Buyer should have had no buys", 0, mBuyer.getBuys().size());
        assertEquals("Buyer should have had no sells", 0, mBuyer.getSells().size());
    }
    
    public void testSaveAndLoad() {
        
    }
    
    private static class TestMarketUser extends Entity implements MarketUser {
        private ArrayList<TestExecution> mBuys;
        private ArrayList<TestExecution> mSells;
        private ArrayList<Market.Bid> mBuyExpirations;
        private ArrayList<Market.Bid> mSellExpirations; 

        public TestMarketUser(EntityKey key, String name, Tick tick) {
            super(key, name, tick);
            mBuys = new ArrayList<TestExecution>();
            mSells = new ArrayList<TestExecution>();
            mBuyExpirations = new ArrayList<Bid>();
            mSellExpirations = new ArrayList<Bid>();
        }

        @Override
        public Entity getEntity() {
            return this;
        }

        @Override
        public void onBuyExecuted(Bid offer, Bid request, int qty) {
            mBuys.add(new TestExecution(request, offer, qty));
        }

        @Override
        public void onOfferExecuted(Bid offer, Bid request, int qty) {
            mSells.add(new TestExecution(request, offer, qty));
        }
        
        public ArrayList<TestExecution> getBuys() {
            return mBuys;
        }
        
        public ArrayList<TestExecution> getSells() {
            return mSells;
        }
        
        
        public ArrayList<Bid> getExpiredBuys() {
            return mBuyExpirations;
        }
        
        public ArrayList<Bid> getgetExpiredSells() {
            return mSellExpirations;
        }

        @Override
        public void onBuyExpired(Bid buy) {
            mBuyExpirations.add(buy);
        }

        @Override
        public void onOfferExpired(Bid offer) {
            mSellExpirations.add(offer);
        }
        
    }
    
    public static class TestExecution {
        public Bid buy;
        public Bid offer;
        public int qty;
        
        public TestExecution(Bid b, Bid o, int p) {
            buy = b;
            offer = o;
            qty = p;
        }
    }
}
