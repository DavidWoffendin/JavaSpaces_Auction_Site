package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/**
 *  Data Object used to store the data for bids in the JavaSpace
 */
public class DIBWBid implements Entry {

    public Integer bidId;
    public Double price;
    public Integer itemId;
    public DIBWUser user;

    /**
     * no args constructor
     */
    public DIBWBid() {}

    /**
     * @param bidId constructor which requires bidId
     */
    public DIBWBid(Integer bidId) {
        this.bidId = bidId;
    }

    /**
     * @param bidId The Id of the bid
     * @param price the price of the bid
     * @param itemId the itemID that the bid should belong to
     * @param user the user who made the bid
     */
    public DIBWBid(Integer bidId, DIBWUser user, Integer itemId, Double price) {
        this.bidId = bidId;
        this.user = user;
        this.itemId = itemId;
        this.price = price;
    }


    public Integer getBidId() {
        return bidId;
    }

    public DIBWBid setBidId(Integer bidId) {
        this.bidId = bidId;
        return this;
    }

    public DIBWUser getUser() {
        return user;
    }

    public DIBWBid setUser(DIBWUser user) {
        this.user = user;
        return this;
    }

    public Integer getItemId() {
        return itemId;
    }

    public DIBWBid setItemId(Integer itemId) {
        this.itemId = itemId;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public DIBWBid setPrice(Double price) {
        this.price = price;
        return this;
    }
}