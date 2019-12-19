package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

/**
 * Lot class designed to keep track of ended lot items
 */
public class DWLotRemover implements Entry {

    public Integer id;
    public Boolean ended;
    public Boolean removed;
    public Boolean boughtOutright;

    /**
     * No args constructor
     */
    public DWLotRemover() {
    }

    /**
     * @param id Lot Id based constructor
     */
    public DWLotRemover(Integer id) {
        this.id = id;
    }

    /**
     * @param id Is the lot ID
     * @param end did the lot end boolean
     * @param remove was the lot removed boolean
     * @param boughtOutright was the lot bought outright boolean
     */
    public DWLotRemover(Integer id, Boolean end, Boolean remove, Boolean boughtOutright) {
        this.id = id;
        this.ended = end;
        this.removed = remove;
        this.boughtOutright = boughtOutright;
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

    public Boolean isBoughtOutright() {
        return boughtOutright;
    }

    public void isBoughtOutright(Boolean boughtOutright) {
        this.boughtOutright = boughtOutright;
    }
}
