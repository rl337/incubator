package org.rl337.economy.data.entity;

import org.rl337.economy.data.Market.Bid;

public interface MarketUser {
    
    public void onOfferExecuted(Bid offer, Bid request, boolean partial);
    public void onBuyExecuted(Bid offer, Bid request, boolean partial);
    
    public void onOfferExpired(Bid offer);
    public void onBuyExpired(Bid buy);
    
    public Entity getEntity();

}
