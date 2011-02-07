package org.rl337.economy.data.entity;

import org.rl337.economy.data.Market.Bid;

public interface MarketUser extends Entity {
    
    public void onOfferExecuted(Bid offer, Bid request, int executedQty);
    public void onBuyExecuted(Bid offer, Bid request, int executedQty);
    
    public void onOfferExpired(Bid offer);
    public void onBuyExpired(Bid buy);
}
