package chat.ono.chatsdk.callback;

import chat.ono.chatsdk.model.Message;
import chat.ono.chatsdk.model.User;

/**
 * Created by kevin on 2018/5/27.
 */

public abstract class MessageCallback {
    abstract public void onMessageReceived(Message message);

    abstract public void onNewFriend(User friend);

    //onConversationUpdate ?
}
