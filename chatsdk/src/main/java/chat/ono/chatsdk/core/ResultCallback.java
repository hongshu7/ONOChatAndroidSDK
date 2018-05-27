package chat.ono.chatsdk.core;

/**
 * Created by kevin on 2018/5/27.
 */

public abstract class ResultCallback<T> {
    abstract public void onSuccess(T result);
    abstract public void onError(int errorCode, String errorMessage);
}
