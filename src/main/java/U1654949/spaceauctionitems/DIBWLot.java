package U1654949.spaceauctionitems;

import net.jini.core.entry.Entry;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 *  Data Object that represents the lots data within the auction system
 */
public class DIBWLot implements Entry {

    public Integer id;
    public Double price;
    public Double buyNowPrice;
    public ArrayList<Integer> bids;
    public String name;
    public String description;
    public DIBWUser user;
    public Boolean ended;
    public Boolean removed;
    public Boolean boughtOutright;

    /**
     * no args constructor
     */
    public DIBWLot() {
    }

    /**
     * Constructor based of ID
     * @param id constructor just using lot id
     */
    public DIBWLot(Integer id) {
        this.id = id;
    }

    /**
     * @param id          lot ID
     * @param price       price of the lot item
     * @param bids        Array of bids against the item
     * @param name        name of the item
     * @param description small description of the item
     * @param user        User who created the lot
     * @param ended       Boolean tracking if the lot ended
     * @param removed     Boolean tracking if lot was removed early
     */
    public DIBWLot(Integer id, DIBWUser user, ArrayList<Integer> bids, String name, Double price, Double buyNowPrice, String description, Boolean ended, Boolean removed, Boolean boughtOutright) {
        this.id = id;
        this.user = user;
        this.bids = bids;
        this.buyNowPrice = buyNowPrice;
        this.name = name;
        this.price = price;
        this.description = description;
        this.ended = ended;
        this.removed = removed;
        this.boughtOutright = boughtOutright;
    }

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);

    public Integer getId() {
        return id;
    }

    public DIBWLot setId(Integer id) {
        this.id = id;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public DIBWLot setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Double getBuyNowPricePrice() {
        return buyNowPrice;
    }

    public DIBWLot setBuyNowPrice(Double buyNowPrice) {
        this.price = buyNowPrice;
        return this;
    }

    public ArrayList<Integer> getBids() {
        if (bids == null) {
            bids = new ArrayList<Integer>();
        }
        return bids;
    }

    public DIBWLot setBids(ArrayList<Integer> bids) {
        this.bids = bids;
        return this;
    }

    public String getName() {
        return name;
    }

    public DIBWLot setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DIBWLot setDescription(String description) {
        this.description = description;
        return this;
    }

    public DIBWUser getUser() {
        return user;
    }

    public String getUserId(){
        return user.getId();
    }

    public DIBWLot setUser(DIBWUser user) {
        this.user = user;
        return this;
    }

    public Boolean isEnded() {
        return ended != null && ended;
    }

    public DIBWLot setEnded(Boolean ended) {
        this.ended = ended;
        return this;
    }

    public Boolean isRemoved() {
        return removed != null && removed;
    }

    public DIBWLot setRemoved(Boolean removed) {
        this.removed = removed;
        return this;
    }

    public Boolean isBoughtOutright() {
        return boughtOutright != null && boughtOutright;
    }

    public DIBWLot setBoughtOutright(Boolean boughtOutright) {
        this.boughtOutright = boughtOutright;
        return this;
    }

    public DIBWLot setOverallRemoval(Boolean ended, Boolean removed, Boolean boughtOutright) {
        this.ended = ended;
        this.removed = removed;
        this.boughtOutright = boughtOutright;
        return this;
    }

    /**
     * @return returns the last bid of the array to be displayed in the gui
     */
    public Integer getLastBid() {
        if (getBids().size() < 1) {
            return null;
        }
        return getBids().get(getBids().size() - 1);
    }

    /**
     * Function to return all useful data as one object
     *
     * @return Data in a single format
     */
    public Object[] asObjectArray() {
        return new Object[]{
                name,
                user == null ? null : user.getId(),
                nf.format(price),
                nf.format(buyNowPrice),
                isEnded() ? "Ended" : "Running"
        };
    }
}
