package linkup.linkup.model;

import java.io.Serializable;

public class Message implements Serializable {
    String message, userId;
    boolean read, sent;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Message() {
        this.read = false;
        this.sent = false;
    }

    public Message(String message, String userId, boolean read, boolean sent) {
        this.message = message;
        this.userId = userId;
        this.read = read;
        this.sent = sent;
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

