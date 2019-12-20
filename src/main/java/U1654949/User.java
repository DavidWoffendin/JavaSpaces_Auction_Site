package U1654949;

import U1654949.spacedataobjects.DIBWUser;

/**
 * Simple class used to extend the User space object for easier use of user information
 * within the local client side auction room
 */
public final class User {

    private static DIBWUser user;

    public static DIBWUser getCurrentUser(){
        return user;
    }

    /**
     * @param username The username that the user will be assigned
     * @return the user class
     */
    public static DIBWUser setCurrentUser(String username){
        user = new DIBWUser(username);
        System.out.println("Username: " + username + ": Is Logged in");
        return user;
    }

}