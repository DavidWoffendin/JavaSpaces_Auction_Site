package com.zackehh.auction.secretary;

/**
 * Inherits the default IWsSecretary. Used only to gain
 * a unique match between IWsLotSecretary and this class
 * in a JavaSpace.
 */
public class IWsBidSecretary extends IWsSecretary {

    public IWsBidSecretary() { super(); }

    public IWsBidSecretary(Integer itemNumber){
        super(itemNumber);
    }

}
