package U1654949.spaceauctionitems;

import java.io.Serializable;
import java.util.Objects;

public class DWUser implements Serializable {

    public final String id;

    public DWUser(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DWUser that = (DWUser) o;

        return Objects.equals(this.id, that.id);
    }
}
