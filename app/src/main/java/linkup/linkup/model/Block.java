package linkup.linkup.model;

import java.io.Serializable;

/**
 * Created by german on 10/8/2017.
 */

public class Block implements Serializable {
    boolean read;
    String by;

    public Block(){

    }
    public Block(boolean read, String by) {
        this.read = read;
        this.by = by;
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
}
