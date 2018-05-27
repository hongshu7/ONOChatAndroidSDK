package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/5/27.
 */

public abstract class Message {
    abstract public int getType();
    abstract public void encode(String data);
    abstract public String decode();
}
