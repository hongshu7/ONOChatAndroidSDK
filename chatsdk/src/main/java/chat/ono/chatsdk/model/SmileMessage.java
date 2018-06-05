package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/6/5.
 */

public class SmileMessage extends Message {
    private String image;
    private int width;
    private int height;

    @Override
    public int getType() {
        return 4;
    }

    @Override
    public String encode() {
        return image + "," + width + "," + height;
    }

    @Override
    public void decode(String data) {
        String[] strs = data.split(",");
        this.image = strs[0];
        this.width = Integer.parseInt(strs[1]);
        this.height = Integer.parseInt(strs[2]);
    }
}
