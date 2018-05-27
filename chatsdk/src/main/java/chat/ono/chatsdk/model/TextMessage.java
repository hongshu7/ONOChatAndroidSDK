package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/5/27.
 */

public class TextMessage extends Message {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void encode(String data) {
        this.text = data;
    }

    @Override
    public String decode() {
        return this.text;
    }


}
