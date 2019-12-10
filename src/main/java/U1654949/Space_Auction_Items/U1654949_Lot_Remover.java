package U1654949.Space_Auction_Items;

import net.jini.core.entry.Entry;

/**
 * Lot class designed to keep track of ended lot items
 */
public class U1654949_Lot_Remover implements Entry {

    public Integer id;
    public Boolean ended;
    public Boolean removed;
    public Boolean boughtOutright;

    /**
     * Empty Constructor
     */
    public U1654949_Lot_Remover() {
    }

    /**
     * @param id Lot Id based constructor
     */
    public U1654949_Lot_Remover(Integer id) {
        this.id = id;
    }

    /**
     * @param id Is the lot ID
     * @param end did the lot end boolean
     * @param remove was the lot removed boolean
     * @param boughtOutright was the lot bought outright boolean
     */
    public U1654949_Lot_Remover(Integer id, Boolean end, Boolean remove) {
        this.id = id;
        this.ended = end;
        this.removed = remove;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public Boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }
}
