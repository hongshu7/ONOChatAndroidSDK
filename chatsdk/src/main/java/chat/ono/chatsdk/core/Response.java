package chat.ono.chatsdk.core;

import com.google.protobuf.Message;
import chat.ono.chatsdk.proto.MessageProtos;

/**
 * Created by kevin on 16/4/20.
 */
public interface Response {
    void successResponse(Message message);
    void errorResponse(MessageProtos.ErrorResponse error);
}
