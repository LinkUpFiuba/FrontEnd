package linkup.linkup.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Message implements Serializable {


    String message, userId;
    boolean read;
    boolean liked;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Message() {
        this.read = false;
        this.liked = false;
    }

    public Message(String message, String userId, boolean read, boolean liked) {
        this.message = message;
        this.userId = userId;
        this.read = read;
        this.liked = liked;
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

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
    @Exclude
    String key;
}

