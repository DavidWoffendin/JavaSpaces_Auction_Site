package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * JavaSpace item used to store bids for a lot
 */
public class DWBid implements Entry {

    public Integer bidId;
    public Double price;
    public Integer itemId;
    public DWUser user;

    /**
     * empty constructor
     */
    public DWBid() {}

    /**
     * @param bidId constructor which requires bidId
     */
    public DWBid(Integer bidId) {
        this.bidId = bidId;
    }

    /**
     * @param bidId The Id of the bid
     * @param price the price of the bid
     * @param itemId the itemID that the bid should belong to
     * @param user the user who made the bid
     */
    public DWBid(Integer bidId, DWUser user, Integer itemId, Double price) {
        this.bidId = bidId;
        this.user = user;
        this.itemId = itemId;
        this.price = price;
    }


    public Integer getBidId() {
        return bidId;
    }

    public DWBid setBidId(Integer bidId) {
        this.bidId = bidId;
        return this;
    }

    public DWUser getUser() {
        return user;
    }

    public DWBid setUser(DWUser user) {
        this.user = user;
        return this;
    }

    public Integer getItemId() {
        return itemId;
    }

    public DWBid setItemId(Integer itemId) {
        this.itemId = itemId;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public DWBid setPrice(Double price) {
        this.price = price;
        return this;
    }
}