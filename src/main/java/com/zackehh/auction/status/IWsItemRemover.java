package com.zackehh.auction.status;

import net.jini.core.entry.Entry;

/**
 * An Entry subclass used to represent items which need
 * to be removed from the UI after being removed from the
 * Space. This enables UI updates after an item may have
 * already been removed from the Space. IWsItemRemover
 * is used in the context of an IWsLot instance being removed
 * from auction either due to Seller removal or the auction
 * being won. The fields in this class represent the way the
 * auction was ended as well as referencing the IWsLot id
 * associated with the changes.
 */
public class IWsItemRemover implements Entry {

    /**
     * The id of the lot being removed.
     */
    public Integer id;

    /**
     * Whether the lot ended due to the item being won.
     */
    public Boolean ended;

    /**
     * Whether the lot ended due to the Seller removing the item.
     */
    public Boolean removed;

    /**
     * Default constructor, used to match any instance of this class in the Space.
     */
    public IWsItemRemover() { }

    /**
     * Constructor to match purely based on id.
     */
    public IWsItemRemover(Integer id){
        this.id = id;
    }

    /**
     * Specific constructor, used to create an instance of this class to be
     * written to the Space.
     *
     * @param id            the id of the associated IWsLot
     * @param ended         whether the lot ended due to a won auction
     * @param removed       whether the lot was removed by the Seller
     */
    public IWsItemRemover(Integer id, Boolean ended, Boolean removed){
        this.id = id;
        this.ended = ended;
        this.removed = removed;
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
     * Public getter for the ended field (in Boolean format).
     *
     * @return Boolean      whether the lot ended due to a won auction
     */
    public Boolean hasEnded(){
        return ended;
    }

    /**
     * Public getter for the removed field (in Boolean format).
     *
     * @return Boolean      whether the lot was removed by the Seller
     */
    public Boolean hasBeenRemoved(){
        return removed;
    }

}
