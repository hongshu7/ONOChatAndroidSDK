package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/6/5.
 */

public class Conversation extends UpdatableModel {
    public static class ConversationType {
        public static int Private = 1;
        public static int Group = 2;
    }

    private int conversationType;
    private double contactTime;
    private int unreadCount;

    private String targetId;
    private String lastMessageId;
    private User user;
    private Message lastMessage;

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public double getContactTime() {
        return contactTime;
    }

    public void setContactTime(double contactTime) {
        if (this.contactTime != contactTime) {
            this.contactTime = contactTime;
            if (isInserted) {
                updates.put("contact_time", contactTime);
            }
        }
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        if (this.unreadCount != unreadCount) {
            this.unreadCount = unreadCount;
            if (isInserted) {
                updates.put("unread_count", unreadCount);
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        if (lastMessage != this.lastMessage) {
            this.lastMessage = lastMessage;
            if (lastMessage == null) {
                setLastMessageId("");
            } else {
                setLastMessageId(lastMessage.getMessageId());
            }
        }
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }


    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        if (lastMessageId == null) {
            lastMessageId = "";
        }
        if (!lastMessageId.equals(this.lastMessageId)) {
            this.lastMessageId = lastMessageId;
            if (isInserted) {
                updates.put("last_message_id", lastMessageId);
            }
        }
    }
}
