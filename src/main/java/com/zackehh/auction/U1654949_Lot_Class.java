package com.zackehh.auction;

import com.zackehh.util.InterfaceUtils;
import net.jini.core.entry.Entry;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The main Lot object to be dealt with when listing the items
 * available in the auction. This object implements Entry to allow
 * storage in a given JavaSpace and provides constructors to provide
 * easy retrieval from the space. Ties to a user so we know who
 * is eligible to accept a given bid.
 */
public class U1654949_Lot_Class implements Entry {

    /**
     * The unique id of this lot item.
     */
    public Integer id;

    /**
     * The current price of this item.
     */
    public Double price;

    /**
     * The list of bid ids (in order) which have been
     * associated with this item.
     */
    public ArrayList<Integer> history;

    /**
     * The name of this item.
     */
    public String name;

    /**
     * The description of this item.
     */
    public String description;

    /**
     * The seller of this item.
     */
    public U1654949_User user;

    /**
     * Whether the auction for this lot has finished.
     */
    public Boolean ended;

    /**
     * Whether the lot is marked for removal.
     */
    public Boolean markedForRemoval;

    /**
     * Default constructor, used to match anything in the space.
     */
    public U1654949_Lot_Class(){ }

    /**
     * Constructor to match purely based on id.
     */
    public U1654949_Lot_Class(Integer id){
        this.id = id;
    }

    /**
     * Template constructor, used to ensure all fields can be set
     * for use when matching specific fields across the space.
     *
     * @param id            the id of this item
     * @param user          the user who created this item
     * @param history       a list of historical bids
     * @param name          the name of the item
     * @param price         the current price of the item
     * @param description   a short description of the item
     * @param ended         whether the lot has ended
     */
    public U1654949_Lot_Class(Integer id, U1654949_User user, ArrayList<Integer> history, String name, Double price, String description, Boolean ended, Boolean markedForRemoval){
        this.id = id;
        this.user = user;
        this.history = history;
        this.name = name;
        this.price = price;
        this.description = description;
        this.ended = ended;
        this.markedForRemoval = markedForRemoval;
    }

    /**
     * Getter for the id of the lot.
     *
     * @return Integer      the numeric id
     */
    public Integer getId(){
        return id;
    }

    /**
     * Getter for the user id of the lot.
     *
     * @return IWsUser      the user
     */
    public U1654949_User getUser(){
        return user;
    }

    /**
     * Getter for the user id of the lot.
     *
     * @return String       the user id
     */
    public String getUserId(){
        return user.getId();
    }

    /**
     * Getter for a history list of the lot. If there
     * is no history, returns a default value.
     *
     * @return String       the history of bids
     */
    public ArrayList<Integer> getHistory() {
        if(history == null){
            history = new ArrayList<Integer>();
        }
        return history;
    }

    /**
     * Getter for the name of the item in the lot.
     *
     * @return String       the item name
     */
    public String getItemName(){
        return name;
    }

    /**
     * Getter for the description of the item in the lot.
     *
     * @return String       the item description
     */
    @SuppressWarnings("unused")
    public String getItemDescription() { return description; }

    /**
     * Getter for the current price of the lot.
     *
     * @return Double       the lot price
     */
    public Double getCurrentPrice(){
        return price;
    }

    /**
     * Getter for the end state of the lot.
     *
     * @return true         if ended
     */
    public Boolean hasEnded(){
        return ended != null && ended;
    }

    /**
     * Getter to determine whether the lot is
     * marked for removal or not. Used to ensure
     * removed items are synced across clients.
     *
     * @return true         if marked for removal
     */
    public Boolean isMarkedForRemoval(){
        return markedForRemoval != null && markedForRemoval;
    }

    /**
     * Public setter for the id field.
     *
     * @param id            the Integer id
     */
    public U1654949_Lot_Class setId(Integer id){
        this.id = id;
        return this;
    }

    /**
     * Public setter for the user field.
     *
     * @param user          the IWsUser user
     */
    public U1654949_Lot_Class setUser(U1654949_User user){
        this.user = user;
        return this;
    }

    /**
     * Public setter for the history field.
     *
     * @param history       the ArrayList history
     */
    @SuppressWarnings("unused")
    public U1654949_Lot_Class setHistory(ArrayList<Integer> history){
        this.history = history;
        return this;
    }

    /**
     * Public setter for the name field.
     *
     * @param name          the String name
     */
    @SuppressWarnings("unused")
    public U1654949_Lot_Class setName(String name){
        this.name = name;
        return this;
    }

    /**
     * Public setter for the price field.
     *
     * @param price         the Double price
     */
    public U1654949_Lot_Class setPrice(Double price){
        this.price = price;
        return this;
    }

    /**
     * Public setter for the description field.
     *
     * @param description   the String description
     */
    @SuppressWarnings("unused")
    public U1654949_Lot_Class setDescription(String description){
        this.description = description;
        return this;
    }

    /**
     * Public setter for the ended field.
     *
     * @param ended         the Boolean ended
     */
    public U1654949_Lot_Class setEnded(Boolean ended){
        this.ended = ended;
        return this;
    }

    /**
     * Public setter for the markedForRemoval field.
     *
     * @param markedForRemoval  the Boolean markedForRemoval
     */
    public U1654949_Lot_Class setMarkedForRemoval(Boolean markedForRemoval){
        this.markedForRemoval = markedForRemoval;
        return this;
    }

    /**
     * Helper to return the last bid associated with the lot.
     * This uses the bid history to return the numeric id of
     * the latest bid.
     *
     * @return Integer      the latest bid id
     */
    public Integer getLatestBid() {
        if(getHistory().size() < 1){
            return null;
        }
        return getHistory().get(getHistory().size() - 1);
    }

    /**
     * Helper to convert the lot to a row of data, which is
     * then used to populate the table of lots in the main
     * views.
     *
     * @return Object[]     the lot as a data row
     */
    public Object[] asObjectArray(){
        return new Object[]{
            id,
            name,
            user == null ? null : user.getId(),
            InterfaceUtils.getDoubleAsCurrency(price),
            hasEnded() ? "Ended" : "Running"
        };
    }

    /**
     * Override Object.equals for use in testing.
     *
     * @param o             the comparison object
     * @return true         if the objects are equal
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        U1654949_Lot_Class that = (U1654949_Lot_Class) o;

        return  Objects.equals(this.id, that.id) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.history, that.history) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.ended, that.ended) &&
                Objects.equals(this.markedForRemoval, that.markedForRemoval);
    }
}
