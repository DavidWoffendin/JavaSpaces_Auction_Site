package U1654949;

import U1654949.Space_Auction_Items.U1654949_User;

public final class User {

    private static U1654949_User user;

    private User() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static U1654949_User getCurrentUser(){
        return user;
    }

    public static U1654949_User setCurrentUser(String username){
        user = new U1654949_User(username);
        System.out.println("Client: " + username + ": Is Registered");
        return user;
    }

}