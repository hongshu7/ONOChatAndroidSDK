package chat.ono.chatsdk.core;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by kevin on 16/4/19.
 */

public class Packet {
    public static final int TYPE_HANDSHAKE = 1;
    public static final int TYPE_HANDSHAKE_ACK = 2;
    public static final int TYPE_HEARTBEAT = 3;
    public static final int TYPE_DATA = 4;
    public static final int TYPE_KICK = 5;


    private Message message;
    private String strings;
    private int type;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getStrings() {
        return strings;
    }

    public void setStrings(String strings) {
        this.strings = strings;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public void decode(DataInputStream is) throws IOException {

        byte[] headers = new byte[4];
        is.readFully(headers, 0, 4);
        //解析头部信息
        this.type = headers[0];
        int length = (headers[1] << 16) | (headers[2] << 8) | (headers[3] >>> 0) & 0xff;

        if (length > 0) {
            byte[] bytes = new byte[length];
            is.readFully(bytes, 0, length);
            if (this.type == TYPE_HANDSHAKE) {
                this.strings = new String(bytes);
            }
            if (this.type == TYPE_DATA) {
                this.message = new Message();
                this.message.decode(bytes);
            }

        }

    }

    public byte[] encode() {
        int length = 0;
        byte[] messageBytes = null;
        if (message != null) {
            messageBytes = message.encode();
            length = messageBytes.length;
        } else if (strings != null) {
            messageBytes = strings.getBytes();
            length = messageBytes.length;
        }
        byte[] bytes = new byte[4 + length];
        bytes[0] = (byte)(this.type & 0xff);
        bytes[1] = (byte)((length >> 16) & 0xff);
        bytes[2] = (byte)((length >> 8) & 0xff);
        bytes[3] = (byte)(length & 0xff);

        if (messageBytes != null) {
            System.arraycopy(messageBytes, 0, bytes, 4, messageBytes.length);
        }
        return bytes;
    }
}
