package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * Counter class used to count certain objects within the space
 */
public class U1654949_Auction_Status_Object implements Entry {

    public Integer lotCounter;
    public Integer bidCounter;

    /**
     * No args Constructor
     */
    public U1654949_Auction_Status_Object() {
    }

    /**
     * @param lotCounter Int to count lots
     * @param bidCounter Int to count bids
     */
    public U1654949_Auction_Status_Object(int lotCounter, int bidCounter) {
        this.lotCounter = lotCounter;
        this.bidCounter = bidCounter;
    }

    public Integer getLotCounter() {
        return lotCounter;
    }

    public void setLotCounter(Integer lotCounter) {
        this.lotCounter = lotCounter;
    }

    public Integer countLot() {
        return ++lotCounter;
    }

    public Integer getBidCounter() {
        return lotCounter;
    }

    public void setBidCounter(Integer lotCounter) {
        this.lotCounter = lotCounter;
    }

    public Integer countBid() {
        return ++lotCounter;
    }

}
