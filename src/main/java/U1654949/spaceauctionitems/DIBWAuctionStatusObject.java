package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/***************************************************************************************
 *    Auction Status Object class. This is designed to keep track of all bids and lots
 *    in the JavaSpace.
 *    Based of Gary Allen's Queue Status Objects
 *
 *    Title: JavaSpacesPrintQueue
 *    Author: Gary Allen
 *    Date: 5/11/2019
 *    Code version: Commit d92df04377da73d0ff4b328a8b0f6e4e47c0ab79
 *    Availability: https://github.com/GaryAllenGit/JavaSpacesPrintQueue
 *
 ***************************************************************************************/
public class DIBWAuctionStatusObject implements Entry {

    public Integer lotCounter;
    public Integer bidCounter;

    /**
     * No args Constructor
     */
    public DIBWAuctionStatusObject() {
    }

    /**
     * @param lotCounter Int to count lots
     * @param bidCounter Int to count bids
     */
    public DIBWAuctionStatusObject(int lotCounter, int bidCounter) {
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
