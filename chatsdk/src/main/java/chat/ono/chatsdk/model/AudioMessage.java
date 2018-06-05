package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/6/5.
 */

public class AudioMessage extends Message {

    private String audio;
    private int duration;

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public String encode() {
        return audio + "," + duration;
    }

    @Override
    public void decode(String data) {
        String[] strs = data.split(",");
        this.audio = strs[0];
        this.duration = Integer.parseInt(strs[1]);
    }
}
