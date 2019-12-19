package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/**
 * Class for handling lot updates
 */
public class DIBWLotUpdate implements Entry {

    public Integer lotId;
    public Double lotPrice;

    /**
     * empty constructor
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

