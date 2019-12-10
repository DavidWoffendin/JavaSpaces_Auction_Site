package com.zackehh.auction.secretary;

/**
 * Inherits the default IWsSecretary. Used only to gain
 * a unique match between IWsBidSecretary and this class
 * in a JavaSpace.
 */
public class IWsLotSecretary extends IWsSecretary {

    public IWsLotSecretary() { super(); }

    public IWsLotSecretary(Integer itemNumber){
        super(itemNumber);
    }

}
