package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * Class for handling lot updates
 */
public class U1654949_Lot_Updater implements Entry {

    public Integer lotId;
    public Double lotPrice;

    /**
     * empty constructor
     */
    public U1654949_Lot_Updater() {
    }

    /**
     * @param id lot is based constructor
     */
    public U1654949_Lot_Updater(Integer id) {
        this.lotId = id;
    }

    /**
     * @param id Lot Id
     * @param price price of lot
     */
    public U1654949_Lot_Updater(Integer id, Double price) {
        this.lotId = id;
        this.lotPrice = price;
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Double getLotPrice() {
        return lotPrice;
    }

    public void setLotPrice(Double lotPrice) {
        this.lotPrice = lotPrice;
    }
}

