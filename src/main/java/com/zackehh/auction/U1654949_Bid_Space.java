package com.zackehh.auction;

import net.jini.core.entry.Entry;

/**
 * JavaSpace item used to store bids for a lot
 */
public class U1654949_Bid_Space implements Entry {

    public Integer bidId;
    public Double price;
    public Integer itemId;
    public U1654949_User user;

    /**
     * empty constructor
     */
    public U1654949_Bid_Space() {}

    /**
     * @param bidId constructor which requires bidId
     */
    public U1654949_Bid_Space(Integer bidId) {
        this.bidId = bidId;
    }

    /**
     * @param bidId The Id of the bid
     * @param price the price of the bid
     * @param itemId the itemID that the bid should belong to
     * @param user the user who made the bid
     */
    public U1654949_Bid_Space(Integer bidId, U1654949_User user, Integer itemId, Double price) {
        this.bidId = bidId;
        this.user = user;
        this.itemId = itemId;
        this.price = price;
    }


    public Integer getBidId() {
        return bidId;
    }

    public U1654949_Bid_Space setBidId(Integer bidId) {
        this.bidId = bidId;
        return this;
    }

    public U1654949_User getUser() {
        return user;
    }

    public U1654949_Bid_Space setUser(U1654949_User user) {
        this.user = user;
        return this;
    }

    public Integer getItemId() {
        return itemId;
    }

    public U1654949_Bid_Space setItemId(Integer itemId) {
        this.itemId = itemId;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public U1654949_Bid_Space setPrice(Double price) {
        this.price = price;
        return this;
    }
}