package U1654949.spaceauctionitems;

import java.io.Serializable;
import java.util.Objects;

/**
 * Data Object to store User Details
 * Could be expanded to store password information and online status
 */
public class DIBWUser implements Serializable {

    public final String id;

    public DIBWUser(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
