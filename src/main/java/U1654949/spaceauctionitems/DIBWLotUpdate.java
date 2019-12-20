package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/**
 *  Data Object for storing bid information in the JavaSpace
 *  Also used to trigger notify
 */
public class DIBWLotUpdate implements Entry {

    public Integer lotId;
    public Double lotPrice;

    /**
     * no args constructor
     */
    public DIBWLotUpdate() {
    }

    /**
     * @param id lot is based constructor
     */
    public DIBWLotUpdate(Integer id) {
        this.lotId = id;
    }

    /**
     * @param id Lot Id
     * @param price price of lot
     */
    public DIBWLotUpdate(Integer id, Double price) {
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

