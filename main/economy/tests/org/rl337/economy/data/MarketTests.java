package org.rl337.economy.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rl337.economy.EntityFactory;
import org.rl337.economy.KeyFactory;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.data.Market.Bid;
import org.rl337.economy.data.entity.MarketUser;
import org.rl337.economy.data.entity.MarketUserImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class MarketTests extends TestCase {
    private Market mMarket;
    private TestMarketUser mSeller;
    private TestMarketUser mBuyer;
    private File mFile;
    private KeyFactory mKeyFactory;
    
    private Injector mInjector;
    
    public void setUp() throws Exception {
        
        mInjector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(KeyFactory.class).asEagerSingleton();
                    bind(EntityFactory.class).asEagerSingleton();
                    bind(Class.class).annotatedWith(Names.named("entityFactory.entityClass")).toInstance(TestMarketUser.class);
                }
            }
        );
        
        EntityFactory entityFactory = mInjector.getInstance(EntityFactory.class);
        mMarket = mInjector.getInstance(Market.class);
        mKeyFactory = mInjector.getInstance(KeyFactory.class);
        mBuyer = (TestMarketUser) entityFactory.newEntity("Buyer");
        mSeller = (TestMarketUser) entityFactory.newEntity("Seller");

        mFile = File.createTempFile("EntityFactoryTests", ".txt");
    }
    
    
    public void tearDown() throws Exception {
        mFile.delete();
    }
    
    public void testSimpleOfferAndBuy() {
        mMarket.offer(mSeller.getKey(), Resource.Food, 10, 250, 50);
        mMarket.buy(mBuyer.getKey(), Resource.Food, 10, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        assertEquals("market should have no more active buys", 0, mMarket.getActiveBuys().length);
        assertEquals("market should have no more active sells", 0, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), buy.buy.getEntityKey());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have nothing in QtyLeft", 0, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), buy.offer.getEntityKey());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), sell.buy.getEntityKey());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), sell.offer.getEntityKey());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have nothing in QtyLeft", 0, sell.offer.getQuantityLeft());
    }

    public void testPartialOffer() {
        mMarket.offer(mSeller.getKey(), Resource.Food, 50, 250, 50);
        mMarket.buy(mBuyer.getKey(), Resource.Food, 10, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        assertEquals("market should have no more active buys", 0, mMarket.getActiveBuys().length);
        assertEquals("market should still have one active sell", 1, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), buy.buy.getEntityKey());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have nothing in QtyLeft", 0, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), buy.offer.getEntityKey());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), sell.buy.getEntityKey());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), sell.offer.getEntityKey());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have 40 QtyLeft", 40, sell.offer.getQuantityLeft());
    }

    public void testPartialBuy() {
        mMarket.offer(mSeller.getKey(), Resource.Food, 10, 250, 50);
        mMarket.buy(mBuyer.getKey(), Resource.Food, 50, 250, 50);
        
        // This should have triggered a buy.
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        assertEquals("market should still have one active buys", 1, mMarket.getActiveBuys().length);
        assertEquals("market should have no active sells", 0, mMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), buy.buy.getEntityKey());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have 40 left in QtyLeft", 40, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), buy.offer.getEntityKey());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), sell.buy.getEntityKey());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), sell.offer.getEntityKey());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have 0 QtyLeft", 0, sell.offer.getQuantityLeft());
    }
    
    public void testMultipleIdenticalOffers() {
        mMarket.offer(mSeller.getKey(), Resource.Food, 1, 10, 3);
        mMarket.offer(mSeller.getKey(), Resource.Food, 1, 10, 3);
        
        assertEquals("Seller should have 2 identical offers", 2, mMarket.getActiveOffers().length);
        
        mMarket.buy(mBuyer.getKey(), Resource.Food, 1, 10, 10);
        mMarket.buy(mBuyer.getKey(), Resource.Food, 1, 10, 10);
        assertEquals("Buyer should have 2 identical buys", 2, mMarket.getActiveBuys().length);
    }
    
    public void testOfferAndBuyExpiration() {
        mMarket.offer(mSeller.getKey(), Resource.Food, 1, 10, 3);
        mMarket.offer(mSeller.getKey(), Resource.Food, 1, 10, 50);

        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        
        assertEquals("Seller should have had 1 expired sell", 1, mSeller.getgetExpiredSells().size());
        assertEquals("Seller should have had no expired buys", 0, mSeller.getExpiredBuys().size());
        assertEquals("Seller should have had no buys", 0, mSeller.getBuys().size());
        assertEquals("Seller should have had no sells", 0, mSeller.getSells().size());
        
        mMarket.buy(mBuyer.getKey(), Resource.Food, 1, 5, 10);

        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        mMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));

        assertEquals("Buyer should have had no expired sells", 0, mBuyer.getgetExpiredSells().size());
        assertEquals("Buyer should have had one expired buy", 1, mBuyer.getExpiredBuys().size());
        assertEquals("Buyer should have had no buys", 0, mBuyer.getBuys().size());
        assertEquals("Buyer should have had no sells", 0, mBuyer.getSells().size());
    }
    
    public void testSaveAndLoad() {
        
        // same test as SimpleOfferAndBuy but we're going to write out the Market, create a new one
        // then use the new one in the test.
        
        mMarket.offer(mSeller.getKey(), Resource.Food, 10, 250, 50);
        mMarket.buy(mBuyer.getKey(), Resource.Food, 10, 250, 50);
        
        assertTrue("market save should have returned true", mMarket.save(mFile));
        Market newMarket = mInjector.getInstance(Market.class);
        assertTrue("new market object load should have returned true", newMarket.load(mFile));
        
        
        // This should have triggered a buy.
        newMarket.executeTick(mKeyFactory.newKey(KeyType.Tick));
        assertEquals("market should have no more active buys", 0, newMarket.getActiveBuys().length);
        assertEquals("market should have no more active sells", 0, newMarket.getActiveOffers().length);
        
        List<TestExecution> sellerSells = mSeller.getSells();
        List<TestExecution> buyerBuys = mBuyer.getBuys();
        assertEquals("Seller should have no buys.", 0, mSeller.getBuys().size());
        assertEquals("Seller should have one sell.", 1, sellerSells.size());
        assertEquals("Buyer should have one buy.", 1, buyerBuys.size());
        assertEquals("Seller should have no sells.", 0, mBuyer.getSells().size());
        
        TestExecution buy = buyerBuys.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), buy.buy.getEntityKey());
        assertEquals("Buy should have executed for a qty of 10", 10, buy.qty);
        assertEquals("Buy's bid should have nothing in QtyLeft", 0, buy.buy.getQuantityLeft());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), buy.offer.getEntityKey());

        TestExecution sell = sellerSells.get(0);
        assertEquals("Buyer in buy exeuction should be buyer", mBuyer.getKey(), sell.buy.getEntityKey());
        assertEquals("Seller in buy exeuction should be seller", mSeller.getKey(), sell.offer.getEntityKey());
        assertEquals("Sell should have executed for a qty of 10", 10, sell.qty);
        assertEquals("Seller's bid should have nothing in QtyLeft", 0, sell.offer.getQuantityLeft());
    }
    
    public static class TestMarketUser extends MarketUserImpl implements MarketUser {
        private ArrayList<TestExecution> mBuys;
        private ArrayList<TestExecution> mSells;
        private ArrayList<Market.Bid> mBuyExpirations;
        private ArrayList<Market.Bid> mSellExpirations; 

        public TestMarketUser() {
            super();
            mBuys = new ArrayList<TestExecution>();
            mSells = new ArrayList<TestExecution>();
            mBuyExpirations = new ArrayList<Bid>();
            mSellExpirations = new ArrayList<Bid>();
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
