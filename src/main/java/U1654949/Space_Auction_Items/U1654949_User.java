package U1654949.Space_Auction_Items;

import java.io.Serializable;
import java.util.Objects;

public class U1654949_User implements Serializable {

    public final String id;

    public U1654949_User(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        U1654949_User that = (U1654949_User) o;

        return Objects.equals(this.id, that.id);
    }
}
