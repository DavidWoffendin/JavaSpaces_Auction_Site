package com.zackehh.auction;

import net.jini.core.entry.Entry;

/**
 * A small tracker to be able to add new lots and bids
 * to the space without having to iterate through the
 * existing objects to find the new id numbers. This will
 * be pulled and written to so it becomes trivial to find
 * the id numbers to associate with new classes.
 */
public class U1654949_Counter_Class implements Entry {

    /**
     * The id of the latest item in the Space.
     */
    public Integer itemNumber;

    /**
     * Default constructor, used to match anything in the space.
     */
    public U1654949_Counter_Class(){ }

    /**
     * Takes a value for the id to start tracking from.
     * This is usually called only once to initialize the
     * first IWsSecretary with 0.
     *
     * @param itemNumber     the item number to start on
     */
    public U1654949_Counter_Class(int itemNumber){
        this.itemNumber = itemNumber;
    }

    /**
     * Getter for the itemNumber property. Returns the id of the
     * latest item to be added to the Space.
     *
     * @return Integer      the item id
     */
    public Integer getItemNumber(){
        return itemNumber;
    }

    /**
     * Helper to increment the itemNumber value and return
     * the new value. Used when a new item will be added
     * to the space.
     *
     * @return Integer      the new item id (after addition)
     */
    public Integer addNewItem(){
        return ++itemNumber;
    }

}
