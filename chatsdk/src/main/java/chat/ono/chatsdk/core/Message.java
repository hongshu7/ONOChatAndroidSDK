package chat.ono.chatsdk.core;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import chat.ono.chatsdk.proto.MessageProtos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hansheng on 18/5/26.
 */
public class Message {
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_NOTIFY = 2;
    public static final int TYPE_RESPONSE = 3;
    public static final int TYPE_PUSH = 4;

    public static final int MSG_COMPRESS_GZIP_MASK = 0x1;
    public static final int MSG_TYPE_MASK = 0x7;
    public static final int MSG_ERROR_MASK = 0x1;



    private int type;
    private int messageId;
    private String route;
    private com.google.protobuf.Message message;
    private boolean isError;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public com.google.protobuf.Message getMessage() {
        return message;
    }

    public void setMessage(com.google.protobuf.Message message) {
        this.message = message;
    }

    public boolean isError() {
        return  isError;
    }

    public void decode(byte[] bytes) {
        int length = bytes.length;
        int offset = 0;
        int flag = bytes[offset++] & 0xff;
        int compressGzip = flag & MSG_COMPRESS_GZIP_MASK;
        this.type = (flag >> 1) & MSG_TYPE_MASK;
        this.isError = ((flag >> 4) & MSG_ERROR_MASK) == 1;
        //NSLog(@"decode length:%d, gzip:%d, type:%d, error:%d", length, compressGzip, self.type, self.isError);

        int msgId = 0;
        if (this.type == TYPE_RESPONSE) {

            int m = bytes[offset] & 0xff;
            int i = 0;
            do {
                m = (bytes[offset] & 0xff);
                msgId = (int) (msgId + ((m & 0x7f) * Math.pow(2, (7 * i))));
                offset++;
                i++;
            } while (m >= 128);

            this.messageId = msgId;
            this.route = IMCore.getInstance().getRouteByMsgId(this.messageId);
        } else if (this.type == TYPE_PUSH) {
            //解析route
            int routeId = bytes[offset++] & 0xff;
            this.route = IMCore.getInstance().getRouteById(routeId);
        }
        Log.v("IM", "decode type:"+type+", route:"+route+(messageId > 0 ? ", msgid:"+messageId : "") + ", body length:"+(length - offset));
        //NSLog(@"decode messageId:%zd, route:%@, body length:%d", self.messageId, self.route, length - offset);
        int bodyLen = length - offset;
        if (bodyLen == 0) {
            return;
        }
        byte[] body = new byte[bodyLen];
        System.arraycopy(bytes, offset, body, 0, bodyLen);

        //解析内容
        if (this.isError) {
            try {
                this.message = MessageProtos.ErrorResponse.parseFrom(body);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            RouteInfo routeInfo = IMCore.getInstance().getRouteInfo(this.route);
            //push将使用resuest
            String messageName = this.type == TYPE_RESPONSE ? routeInfo.getResponse() : routeInfo.getRequest();
            Log.v("IM", "decode with messageName:"+messageName);
            if (messageName != null) {
                String fullClassName = "chat.ono.chatsdk.proto.MessageProtos$" + messageName;
                try {
                    Class clazz = Class.forName(fullClassName);
                    if (body != null) {
                        Method m = clazz.getMethod("parseFrom", new Class[]{byte[].class});
                        this.message = (com.google.protobuf.Message)m.invoke(clazz, body);
                    } else {
                        this.message = (com.google.protobuf.Message)clazz.newInstance();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public byte[] encode() {
        int headHength = 2; //flag + route
        if (this.type == TYPE_REQUEST) {
            //msgid
            headHength += Message.caculateMsgIdBytes(this.messageId);
        }

        byte[] headBytes = new byte[headHength];
        //执行request
        boolean compressGzip = false;
        //flag
        int offset = 0;
        headBytes[offset++] = (byte)((this.type << 1) | (compressGzip ? 1 : 0));
        if (this.type == TYPE_REQUEST) {
            //写入message id
            offset = Message.encodeMsgId(this.messageId, headBytes, offset);
        }
        RouteInfo routeInfo = IMCore.getInstance().getRouteInfo(this.route);
        if (routeInfo != null) {
            headBytes[offset++] = (byte)routeInfo.getRouteId();
        }

        Log.v("IM", "encode type:"+type+", route:"+route+", routeid:"+routeInfo.getRouteId()+", msgid:"+messageId);

        if (this.message == null) {
            return headBytes;
        } else {
            byte[] bodyBytes = this.message.toByteArray();
            byte[] bytes = new byte[headBytes.length + bodyBytes.length];
            System.arraycopy(headBytes, 0, bytes, 0, headHength);
            System.arraycopy(bodyBytes, 0, bytes, headHength, bodyBytes.length);
            return bytes;
        }
    }

    public static int encodeMsgId(int id, byte[] buffer, int offset) {
        do {
            int tmp = id % 128;
            int next = (int) Math.floor(id / 128);
            if (next != 0) {
                tmp = tmp + 128;
            }
            buffer[offset++] = (byte) tmp;
            id = next;
        } while (id != 0);
        return offset;
    }

    public static int caculateMsgIdBytes(int id) {
        int len = 0;
        do {
            len += 1;
            id >>= 7;
        } while (id > 0);
        return len;
    }
}