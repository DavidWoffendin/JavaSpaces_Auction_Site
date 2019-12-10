package com.zackehh.auction.status;

import net.jini.core.entry.Entry;

/**
 * A subclass of Entry used to represent a change being made
 * to an IWsLot instance in the Space. Used to ensure that
 * the UI is updated across all clients when an IWsLot instance
 * is updated, in order to account for multiple users attempting
 * to bid on the same auction lot at the same time. The id field
 * in this class is a reference to the id of the IWsLot it is
 * associated with, and all other fields (currently just 'price')
 * refer to the changes which have been made to the IWsLot and
 * represent the changes which should be displayed on the UI.
 */
public class IWsLotChange implements Entry {

    /**
     * The id of the lot being changed.
     */
    public Integer id;
    public Double price;

    /**
     * Default constructor, used to match any instance of this class in the Space.
     */
    public IWsLotChange(){ }

    /**
     * Constructor to match purely based on id.
     */
    public IWsLotChange(Integer id){
        this.id = id;
    }

    /**
     * Specific constructor, used to create an instance of this class to be
     * written to the Space.
     *
     * @param id            the id of the associated IWsLot
     * @param price         the new price of the item
     */
    public IWsLotChange(Integer id, Double price){
        this.id = id;
        this.price = price;
    }

    /**
     * Public getter for the id field.
     *
     * @return Integer      the id of the IWsLot
     */
    public Integer getId(){
        return id;
    }

    /**
     * Public getter for the price field.
     *
     * @return Double       the new price of the item
     */
    public Double getPrice(){
        return price;
    }

}
