package com.zackehh.auction;

import com.zackehh.util.UserUtils;
import net.jini.core.entry.Entry;

import java.util.Objects;

/**
 * The main Bid object to be dealt with when bidding on lots.
 * This object implements Entry to allow storage in a given JavaSpace
 * and provides constructors to provide easy retrieval from the space.
 * Ties to a user and keeps track of whether the bid was a private
 * bid or not.
 */
public class U1654949_Bid_Class implements Entry {

    /**
     * Tracks whether the bid is anonymous.
     */
    public Boolean visible;

    /**
     * The unique id of the bid object.
     */
    public Integer id;

    /**
     * The price the user entered when bidding.
     */
    public Double price;

    /**
     * The item id the bid object should be associated with.
     */
    public Integer itemId;

    /**
     * The user who bid on the item.
     */
    public U1654949_User_Class user;

    /**
     * Default constructor, used to match anything in the space.
     */
    public U1654949_Bid_Class(){ }

    /**
     * Constructor to match purely based on id.
     */
    public U1654949_Bid_Class(Integer id){
        this.id = id;
    }

    /**
     * Template constructor, used to ensure all fields can be set
     * for use when matching specific fields across the space.
     *
     * @param id            the id of this bid
     * @param user          the user associated with this bid
     * @param itemId        the item id this bid is associated with
     * @param price         the price the user has bid on the item
     * @param visible       whether this bid is anonymous or not
     */
    public U1654949_Bid_Class(Integer id, U1654949_User_Class user, Integer itemId, Double price, Boolean visible){
        this.id = id;
        this.user = user;
        this.itemId = itemId;
        this.price = price;
        this.visible = visible;
    }

    /**
     * Getter for the id field.
     *
     * @return Integer      the bid id
     */
    public Integer getId(){
        return id;
    }

    /**
     * Getter for the userId field.
     *
     * @return String       the user id
     */
    public U1654949_User_Class getUser() {
        return user;
    }

    /**
     * Getter for the itemId field.
     *
     * @return Integer      the item id
     */
    public Integer getItemId(){
        return itemId;
    }

    /**
     * Getter for the maxPrice field.
     *
     * @return Double       the max price
     */
    public Double getPrice(){
        return price;
    }

    /**
     * Getter for the isPublic field.
     *
     * @return true         if it's a public bid
     */
    public Boolean isPublic(){
        return visible;
    }

    /**
     * Shorthand for checking if this bid should be
     * visible to the current user.
     *
     * @return true     if the bid is anonymous
     */
    public Boolean isAnonymous(U1654949_Lot_Class lot) {
        return  lot.getId().equals(getItemId()) &&
                !isPublic() &&
                !UserUtils.getCurrentUser().equals(getUser()) &&
                !UserUtils.getCurrentUser().equals(lot.getUser());
    }

    /**
     * Public setter for the id field.
     *
     * @param id        the Integer id
     */
    public U1654949_Bid_Class setId(Integer id){
        this.id = id;
        return this;
    }

    /**
     * Public setter for the user field.
     *
     * @param user      the IWsUser user
     */
    public U1654949_Bid_Class setUser(U1654949_User_Class user){
        this.user = user;
        return this;
    }

    /**
     * Public setter for the itemId field.
     *
     * @param itemId    the Integer itemId
     */
    @SuppressWarnings("unused")
    public U1654949_Bid_Class setItemId(Integer itemId){
        this.itemId = itemId;
        return this;
    }

    /**
     * Public setter for the price field.
     *
     * @param price     the Double price
     */
    @SuppressWarnings("unused")
    public U1654949_Bid_Class setPrice(Double price){
        this.price = price;
        return this;
    }

    /**
     * Public setter for the visible field.
     *
     * @param visible   the Boolean visible
     */
    @SuppressWarnings("unused")
    public U1654949_Bid_Class setVisible(Boolean visible){
        this.visible = visible;
        return this;
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

        U1654949_Bid_Class that = (U1654949_Bid_Class) o;

        return  Objects.equals(this.visible, that.visible) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.itemId, that.itemId) &&
                Objects.equals(this.user, that.user);
    }

}