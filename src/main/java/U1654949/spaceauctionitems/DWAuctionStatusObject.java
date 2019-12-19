package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/**
 * Counter class used to count certain objects within the space
 */
public class DWAuctionStatusObject implements Entry {

    public Integer lotCounter;
    public Integer bidCounter;

    /**
     * No args Constructor
     */
    public DWAuctionStatusObject() {
    }

    /**
     * @param lotCounter Int to count lots
     * @param bidCounter Int to count bids
     */
    public DWAuctionStatusObject(int lotCounter, int bidCounter) {
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
        return bidCounter;
    }

    public void setBidCounter(Integer lotCounter) {
        this.bidCounter = bidCounter;
    }

    public Integer countBid() {
        return ++bidCounter;
    }

}
