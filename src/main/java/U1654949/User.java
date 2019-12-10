package U1654949;

import U1654949.Space_Auction_Items.U1654949_User;

/**
 * An extremely simple class to store and keep track of the
 * username of the current user. This class will only contain
 * a getter and setter for `username`, and everything should
 * be accessed statically.
 */
public final class User {

    /**
     * The currently registered user in the client. This should
     * not change throughout the lifecycle of the application,
     * however this is provided so we have the potential to.
     */
    private static U1654949_User user;

    /**
     * Default constructor which should not be called. All variables
     * and properties should be accessed statically.
     */
    private User(){
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the current username associated with the application.
     * This should never be null, but it's not guaranteed.
     *
     * @return String       the current user
     */
    public static U1654949_User getCurrentUser(){
        return user;
    }

    /**
     * Sets the current username associated with the application. This
     * is called only after the initialization in IWsAuctionRoom#main.
     *
     * @param username      the username to set
     */
    public static U1654949_User setCurrentUser(String username){
        user = new U1654949_User(username);
        System.out.println("Registered client for user: " + username);
        return user;
    }

}
