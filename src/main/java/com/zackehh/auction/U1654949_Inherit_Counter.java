package com.zackehh.auction;

import net.jini.core.entry.Entry;

/**
 * Counter class used to count certain objects within the space
 */
public class U1654949_Inherit_Counter implements Entry {

    public Integer itemCounter;

    /**
     * empty constructor
     */
    public U1654949_Inherit_Counter() {
    }

    /**
     * @param itemCounter constructor requiring itemCounter
     */
    public U1654949_Inherit_Counter(int itemCounter) {
        this.itemCounter = itemCounter;
    }

    public Integer getItemCounter() {
        return itemCounter;
    }

    public void setItemCounter(Integer itemCounter) {
        this.itemCounter = itemCounter;
    }

    public Integer countNewItem() {
        return ++itemCounter;
    }

}
