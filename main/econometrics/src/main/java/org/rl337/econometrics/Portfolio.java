package org.rl337.econometrics;

import java.security.InvalidParameterException;

public class Portfolio {
    private Asset[] mAssets;
    private double[] mAllocation;
    private double mEndowment;
    
    public Portfolio(Asset[] assets, double[] shares, Period purchased) {
        
        if (assets.length != shares.length) {
            throw new InvalidParameterException("assets and shares must be the same size");
        }
        
        double worth[] = new double[assets.length];
        double endowment = 0;
        for(int i = 0; i < assets.length; i++) {
            worth[i] = assets[i].price(purchased) * shares[i];
            endowment += worth[i];
        }
        
        for(int i = 0; i < assets.length; i++) {
            worth[i] /= endowment;
        }
        
        init(assets, worth, endowment);
    }
    
    public Portfolio(Asset[] assets, double endowment, double[] allocation) {
        init(assets, allocation, endowment);
    }
    
    private void init(Asset[] assets, double[] allocation, double endowment) {
        mAssets = assets;
        
        if (allocation.length != allocation.length) {
            throw new InvalidParameterException("Asset count and shares count must match");
        }
        
        double total = 0;
        for(int i = 0; i < allocation.length; i++) {
            total += allocation[i];
        }
        
        if (total != 1.0) {
            throw new InvalidParameterException("Sum of allocation should be 1.0");
        }
        
        mAllocation = allocation;
        mEndowment = endowment;
    }
    
    public double getGrossReturnRate(Period start, Period stop) {
        double combinedReturn = 1;
        for(int i = 0; i < mAssets.length; i++) {
            Asset a = mAssets[i];
            double assetReturn = a.getNetReturn(start, stop);
            double grossAssetReturn = assetReturn * mAllocation[i];
            
            combinedReturn += grossAssetReturn;
        }
        
        return combinedReturn;
    }
    
    public double getNetReturnRate(Period start, Period stop) {
        double grossReturnRate = getGrossReturnRate(start, stop);
        return grossReturnRate - 1;

    }
    
    public double getGrossReturn(Period start, Period stop) {
        double combinedReturn = getGrossReturnRate(start, stop);
        
        return combinedReturn * mEndowment;
    }
    
    public double getNetReturn(Period start, Period stop) {
        double netReturnRate = getNetReturnRate(start, stop);
        return netReturnRate * mEndowment;
    }

    public double getContinuallyCompoundedNetRate(Period start, Period stop) {
        return Math.log(getGrossReturnRate(start, stop));
    }
}
