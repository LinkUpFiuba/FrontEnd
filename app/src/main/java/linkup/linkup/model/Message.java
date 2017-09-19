package linkup.linkup.model;

import java.io.Serializable;

public class Message implements Serializable {
    String message, userId;
    boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Message() {
        this.read = false;
    }

    public Message(String message, String userId, boolean read) {
        this.message = message;
        this.userId = userId;
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

