package U1654949.spacedataobjects;

import net.jini.core.entry.Entry;

/**
 *  Data Object used to store the conditions of a lot ending
 *  Also used to trigger notify
 */
public class DIBWLotRemove implements Entry {

    public Integer id;
    public String buyerName;
    public Boolean ended;
    public Boolean removed;
    public Boolean boughtOutright;

    /**
     * No args constructor
     */
    public DIBWLotRemove() {
    }

    /**
     * @param id Lot Id based constructor
     */
    public DIBWLotRemove(Integer id) {
        this.id = id;
    }

    /**
     * @param id Is the lot ID
     * @param buyerName Is the name of the user who bought the lot
     * @param end did the lot end boolean
     * @param remove was the lot removed boolean
     * @param boughtOutright was the lot bought outright boolean
     */
    public DIBWLotRemove(Integer id, String buyerName, Boolean end, Boolean remove, Boolean boughtOutright) {
        this.id = id;
        this.buyerName = buyerName;
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

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String id) {
        this.buyerName = buyerName;
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
