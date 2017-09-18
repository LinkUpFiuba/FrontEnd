package linkup.linkup.model;

import java.io.Serializable;

import static android.R.attr.name;

public class ChatRoom implements Serializable {
    String id, lastMessage, timestamp;
    SerializableUser user;
    int unreadCount;

    public ChatRoom() {
    }

    public ChatRoom(String id, String lastMessage, String timestamp, int unreadCount, SerializableUser user) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SerializableUser getUser() {
        return user;
    }

    public void setUser(SerializableUser user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Long getTimestampLong() {
        return Long.parseLong(timestamp);
    }


    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
