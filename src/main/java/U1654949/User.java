package U1654949;

import U1654949.spaceauctionitems.DIBWUser;

/**
 * Simple class used to extend the User space object for easier use within the local auction room
 */
public final class User {

    private static DIBWUser user;

    private User() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static DIBWUser getCurrentUser(){
        return user;
    }

    /**
     * @param username The username that the user will be assigned
     * @return the user class
     */
    public static DIBWUser setCurrentUser(String username){
        user = new DIBWUser(username);
        System.out.println("Client: " + username + ": Is Registered");
        return user;
    }

}