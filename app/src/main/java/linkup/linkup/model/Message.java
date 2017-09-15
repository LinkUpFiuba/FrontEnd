package linkup.linkup.model;

import java.io.Serializable;

import linkup.linkup.model.User;

public class Message implements Serializable {
    String message, userId;

    public Message() {
    }

    public Message(String message, String userId) {

        this.message = message;
        this.userId = userId;
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

