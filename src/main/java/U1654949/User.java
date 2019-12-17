package U1654949;

import U1654949.Space_Auction_Items.DWUser;

/**
 * Simple class used to extend the User space object for easier use within the local auction room
 */
public final class User {

    private static DWUser user;

    private User() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static DWUser getCurrentUser(){
        return user;
    }

    /**
     * @param username The username that the user will be assigned
     * @return the user class
     */
    public static DWUser setCurrentUser(String username){
        user = new DWUser(username);
        System.out.println("Client: " + username + ": Is Registered");
        return user;
    }

}