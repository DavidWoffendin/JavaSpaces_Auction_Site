package com.zackehh.auction;

import java.io.Serializable;
import java.util.Objects;

/**
 * A basic user class to contain the id of the user passed into
 * the constructor. This isn't necessarily needed, however it
 * allows for easier expansion in the case of user passwords
 * and other user based properties.
 */
public class U1654949_User_Class implements Serializable {

    /**
     * The id of the user
     */
    public final String id;

    /**
     * Set up a user with the given properties
     *
     * @param id        the user id
     */
    public U1654949_User_Class(String id){
        this.id = id;
    }

    /**
     * Getter for the id, in case of transformations
     *
     * @return String   the user id
     */
    public String getId(){
        return id;
    }

    /**
     * Override Object.equals for use in testing.
     *
     * @param o             the comparison object
     * @return true         if the objects are equal
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        U1654949_User_Class that = (U1654949_User_Class) o;

        return Objects.equals(this.id, that.id);
    }
}
