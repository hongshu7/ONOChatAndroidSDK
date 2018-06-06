package chat.ono.chatsdk.callback;

import chat.ono.chatsdk.proto.MessageProtos;

/**
 * Created by kevin on 2018/5/27.
 */

public interface FailureCallback {

    void onError(Error error);
}
