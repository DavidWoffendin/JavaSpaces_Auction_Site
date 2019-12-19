package U1654949.spaceauctionitems;

import java.io.Serializable;
import java.util.Objects;

public class DIBWUser implements Serializable {

    public final String id;

    public DIBWUser(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DIBWUser that = (DIBWUser) o;

        return Objects.equals(this.id, that.id);
    }
}
