package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * Counter class used to count certain objects within the space
 */
public class U1654949_Bid_Status_Object implements Entry {

    public Integer bidCounter;

    /**
     * No args Constructor
     */
    public U1654949_Bid_Status_Object() {
    }

    /**
     * @param itemCounter constructor requiring itemCounter
     */
    public U1654949_Bid_Status_Object(int itemCounter) {
        this.bidCounter = itemCounter;
    }

    public Integer getItemCounter() {
        return bidCounter;
    }

    public void setItemCounter(Integer itemCounter) {
        this.bidCounter = itemCounter;
    }

    public Integer countNewItem() {
        return ++bidCounter;
    }

}
