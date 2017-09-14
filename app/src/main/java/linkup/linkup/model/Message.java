package linkup.linkup.model;

import java.io.Serializable;

import linkup.linkup.model.User;

public class Message implements Serializable {
    String id, message, createdAt;
    SerializableUser user;

    public Message() {
    }

    public Message(String id, String message, String createdAt, SerializableUser user) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SerializableUser getUser() {
        return user;
    }

    public void setUser(SerializableUser user) {
        this.user = user;
    }
}
