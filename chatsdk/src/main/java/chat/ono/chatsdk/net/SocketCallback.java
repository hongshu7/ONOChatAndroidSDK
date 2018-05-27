package chat.ono.chatsdk.net;

import chat.ono.chatsdk.core.Packet;

/**
 * Created by kevin on 16/4/19.
 */
public interface SocketCallback {
    public void handleMessage(Packet packet);
}
