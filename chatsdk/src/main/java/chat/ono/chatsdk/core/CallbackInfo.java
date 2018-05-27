package chat.ono.chatsdk.core;

/**
 * Created by kevin on 16/4/20.
 */
public class CallbackInfo {
    private int listenerId;
    private IMCallback callback;

    public int getListenerId() {
        return listenerId;
    }

    public void setListenerId(int listenerId) {
        this.listenerId = listenerId;
    }


    public IMCallback getCallback() {
        return callback;
    }

    public void setCallback(IMCallback callback) {
        this.callback = callback;
    }
}
