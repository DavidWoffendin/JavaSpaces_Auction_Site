package U1654949.Space_Auction_Items;

import U1654949.User_Interface.Interface_Helpers.Common_Functions;
import net.jini.core.entry.Entry;

import java.util.ArrayList;

/**
 * Class that represents the lots within the auction system
 */
public class U1654949_Lot implements Entry {

    public Integer id;
    public Double price;
    public Double buyNowPrice;
    public ArrayList<Integer> bids;
    public String name;
    public String description;
    public U1654949_User user;
    public Boolean ended;
    public Boolean removed;

    /**
     * empty constructor
     */
    public U1654949_Lot() {
    }

    /**
     * @param id constructor just using lot id
     */
    public U1654949_Lot(Integer id) {
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
    public U1654949_Lot(Integer id, U1654949_User user, ArrayList<Integer> bids, String name, Double price, Double buyNowPrice, String description, Boolean ended, Boolean removed) {
        this.id = id;
        this.user = user;
        this.bids = bids;
        this.buyNowPrice = buyNowPrice;
        this.name = name;
        this.price = price;
        this.description = description;
        this.ended = ended;
        this.removed = removed;
    }


    public Integer getId() {
        return id;
    }

    public U1654949_Lot setId(Integer id) {
        this.id = id;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public U1654949_Lot setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Double getBuyNowPricePrice() {
        return buyNowPrice;
    }

    public U1654949_Lot setBuyNowPrice(Double buyNowPrice) {
        this.price = buyNowPrice;
        return this;
    }

    public ArrayList<Integer> getBids() {
        if (bids == null) {
            bids = new ArrayList<Integer>();
        }
        return bids;
    }

    public U1654949_Lot setBids(ArrayList<Integer> bids) {
        this.bids = bids;
        return this;
    }

    public String getName() {
        return name;
    }

    public U1654949_Lot setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public U1654949_Lot setDescription(String description) {
        this.description = description;
        return this;
    }

    public U1654949_User getUser() {
        return user;
    }

    public String getUserId(){
        return user.getId();
    }

    public U1654949_Lot setUser(U1654949_User user) {
        this.user = user;
        return this;
    }

    public Boolean isEnded() {
        return ended != null && ended;
    }

    public U1654949_Lot setEnded(Boolean ended) {
        this.ended = ended;
        return this;
    }

    public Boolean isRemoved() {
        return removed != null && removed;
    }

    public U1654949_Lot setRemoved(Boolean removed) {
        this.removed = removed;
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
                id,
                name,
                user == null ? null : user.getId(),
                Common_Functions.getDoubleAsCurrency(price),
                isEnded() ? "Ended" : "Running"
        };
    }
}
