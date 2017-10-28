package linkup.linkup.model;

import java.io.Serializable;

/**
 * Created by german on 10/8/2017.
 */

public class Block implements Serializable {
    public static final String BLOCK = "BLOCK";
    public static final String DELETE_LINK = "DELETE";

    boolean read;
    String by;

    String type;

    public Block(){

    }
    public Block(boolean read, String by) {
        this.read = read;
        this.by = by;
        this.type = BLOCK;
    }

    public boolean isRead() {

        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
