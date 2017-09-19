package linkup.linkup.model;

import java.io.Serializable;

public class Match implements Serializable {
    boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public Match() {
        this.read = false;
    }

    public Match(boolean read) {
        this.read = read;
    }
}

