package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/5/27.
 */

public abstract class Message extends UpdatableModel {
    private String messageId;
    private String targetId;
    private String userId;
    private double timestamp;
    private Boolean isSend;
    private Boolean isSelf;
    private Boolean isError;

    private User user;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        if (messageId != null && !messageId.equals(this.messageId)) {
            this.messageId = messageId;
            if (isInserted) {
                updates.put("message_id", messageId);
            }
        }
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean isSend() {
        return isSend;
    }

    public void setSend(Boolean send) {
        if (send != this.isSend) {
            this.isSend = send;
            if (isInserted) {
                updates.put("is_send", send);
            }
        }
    }

    public Boolean isSelf() {
        return isSelf;
    }

    public void setSelf(Boolean self) {
        isSelf = self;
    }

    public Boolean isError() {
        return isError;
    }

    public void setError(Boolean error) {
        if (error != this.isError) {
            this.isError = error;
            if (isInserted) {
                updates.put("is_error", error);
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    abstract public int getType();
    abstract public String encode();
    abstract public void decode(String data);


}
