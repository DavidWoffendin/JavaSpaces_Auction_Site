package U1654949;

/**
 * Various Constants used throughout the application. Used
 * just to keep things in sync, and removing the need to modify
 * values across a bunch of classes; rather just change it here.
 */
public final class Default_Variables {

    /**
     * Default constructor which should not be called. All variables
     * are static and final.
     */
    private Default_Variables(){
        throw new UnsupportedOperationException();
    }

    /**
     * The title of the application
     */
    public static final String APPLICATION_TITLE = "Auction Room";

    /**
     * The title of the main lot list card
     */
    public static final String AUCTION_CARD = "Auction";

    /**
     * The title of the card showing bid details for a lot
     */
    public static final String BID_CARD = "Bid";

    /**
     * A regular expression to validate a user-defined currency
     */
    public static final String CURRENCY_REGEX = "(?=.)^\\$?(([1-9]" +
            "[0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?(\\.[0-9]{1,2})?$";

    /**
     * Default expiry time for a Lot Lease, to avoid needed cleanup
     */
    public static final long LOT_LEASE_TIMEOUT = 1000 * 60 * 60;

    /**
     * Default expiry time for a Bid Lease, to avoid needed cleanup
     */
    public static final long BID_LEASE_TIMEOUT = Math.round(LOT_LEASE_TIMEOUT * 1.5);

    /**
     * The default timeout for any JavaSpace related operations
     */
    public static final long SPACE_TIMEOUT = 1500;

    /**
     * The time to wait for a generic object removal from the space
     */
    public static final long TEMP_OBJECT = SPACE_TIMEOUT * 2;

}
