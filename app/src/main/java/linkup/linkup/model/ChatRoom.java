package linkup.linkup.model;

import java.io.Serializable;

public class ChatRoom implements Serializable {
    String id, lastMessage;
    SerializableUser user;
    boolean read = false;
    int unreadCount;

    public ChatRoom() {
        this.read = false;
    }

    public ChatRoom(String id, String lastMessage, int unreadCount, SerializableUser user) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.read = false;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
}
