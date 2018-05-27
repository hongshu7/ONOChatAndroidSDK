package chat.ono.chatsdk.core;

import com.google.protobuf.Message;

/**
 * Created by kevin on 16/4/20.
 */
public interface IMCallback {
    void callback(Message message);
}
