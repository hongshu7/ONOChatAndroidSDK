package chat.ono.chatsdk.core;

/**
 * Created by kevin on 2018/5/27.
 */

public abstract class MessageCallback {
    abstract public void onReceived(chat.ono.chatsdk.model.Message message);
}
