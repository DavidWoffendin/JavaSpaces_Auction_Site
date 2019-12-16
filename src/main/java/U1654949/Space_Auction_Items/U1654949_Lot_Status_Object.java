package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * Counter class used to count certain objects within the space
 */
public class U1654949_Lot_Status_Object implements Entry {

    public Integer itemCounter;

    /**
     * No args Constructor
     */
    public U1654949_Lot_Status_Object() {
    }

    /**
     * @param itemCounter constructor requiring itemCounter
     */
    public U1654949_Lot_Status_Object(int itemCounter) {
        this.itemCounter = itemCounter;
    }

    public Integer getItemCounter() {
        return itemCounter;
    }

    public void setItemCounter(Integer itemCounter) {
        this.itemCounter = itemCounter;
    }

    public Integer countNewItem() {
        return ++itemCounter;
    }

}
