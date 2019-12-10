package U1654949;

/**
 * A class of commonly used defaults
 */
public final class Default_Variables {

    private Default_Variables(){
        throw new UnsupportedOperationException();
    }

    public static final String HOST_NAME = "waterloo";

    public static final String APPLICATION_TITLE = "Auction Application";

    public static final String AUCTION_CARD = "Auction";

    public static final String BID_CARD = "Bid";

    public static final String CURRENCY_REGEX = "(?=.)^\\$?(([1-9]" +
            "[0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?(\\.[0-9]{1,2})?$";

    public static final long LOT_LEASE_TIMEOUT = 1000 * 60 * 60;

    public static final long BID_LEASE_TIMEOUT = Math.round(LOT_LEASE_TIMEOUT * 1.5);

    public static final long SPACE_TIMEOUT = 1500;

    public static final long TEMP_OBJECT = SPACE_TIMEOUT * 2;

}