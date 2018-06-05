package chat.ono.chatsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.Message;

import org.apache.commons.beanutils.BeanUtils;

import java.util.List;

import chat.ono.chatsdk.core.DB;
import chat.ono.chatsdk.core.IMCallback;
import chat.ono.chatsdk.core.IMCore;
import chat.ono.chatsdk.core.MessageCallback;
import chat.ono.chatsdk.core.Response;
import chat.ono.chatsdk.core.ResultCallback;
import chat.ono.chatsdk.model.AudioMessage;
import chat.ono.chatsdk.model.Conversation;
import chat.ono.chatsdk.model.ImageMessage;
import chat.ono.chatsdk.model.SmileMessage;
import chat.ono.chatsdk.model.TextMessage;
import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.proto.MessageProtos;
import chat.ono.chatsdk.utils.ObjectId;

/**
 * Created by kevin on 2018/5/27.
 */

public class IMClient {

    static {
        IMCore.getInstance().addPushListener("push.message", new IMCallback() {
            @Override
            public void callback(Message message) {
                receiveMessage((chat.ono.chatsdk.proto.MessageProtos.Message)message, true);
            }
        });
    }

    private static Context context;
    public static Context getContext() {
        return context;
    }
    public static void setContext(Context context) {
        IMClient.context = context;
    }


    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    private static MessageCallback messageCallback;
    public static void setMessageCallback(MessageCallback mc) {
        messageCallback = mc;
    }

    private static void receiveMessage(chat.ono.chatsdk.proto.MessageProtos.Message msg, boolean dispatch) {
        chat.ono.chatsdk.model.Message message = createMessageFromType(msg.getType());

        message.setMessageId(msg.getMid());
        message.setTimestamp(System.currentTimeMillis());
        message.setSend(true);
        message.setSelf(IMCore.getInstance().getUserId().equals(msg.getFrom()));
        String targetId = message.isSelf() ? msg.getTo() : msg.getFrom();
        message.setTargetId(targetId);
        //message.setUser(IMCore.getInstance().getUser());
        message.setUserId(msg.getFrom());
        message.decode(msg.getData());

        //save message
        DB.addMessage(message);

        //update conversion
        Conversation conversation = getConversation(targetId);
        conversation.setLastMessage(message);
        conversation.setContactTime(System.currentTimeMillis());
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);
        if (conversation.isInserted()) {
            DB.updateConversation(conversation);
        } else {
            DB.addConversation(conversation);
        }

        //callback
        if (dispatch && messageCallback != null) {
            messageCallback.onReceived(message);
        }

    }

    public static String generateMessageId() {
        return ObjectId.get().toString();
    }

    public static chat.ono.chatsdk.model.Message createMessageFromType(int type) {

        switch (type) {
            case 1:
                return new TextMessage();
            case 2:
                return new AudioMessage();
            case 3:
                return new ImageMessage();
            case 4:
                return new SmileMessage();
            default:
                //todo: custom message
                break;

        }
        return null;
    }

    private static Conversation getConversation(String targetId) {
        return getConversation(targetId, Conversation.ConversationType.Private);
    }

    private static Conversation getConversation(String targetId, int conversationType) {
        Conversation conversation = DB.fetchConversation(targetId);
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setConversationType(conversationType);
            conversation.setTargetId(targetId);
        }
        return conversation;
    }

    public static void setup(String host, int port) {
        IMCore.getInstance().setup(host, port);
    }

    public static void connect(String token, final ResultCallback<User> callback) {
        IMCore.getInstance().login(token, new Response() {
            @Override
            public void successResponse(Message message) {
                MessageProtos.UserLoginResponse response = (MessageProtos.UserLoginResponse)message;
                MessageProtos.UserData userData = response.getUser();
                //获取自身信息
                User user = DB.fetchUser(userData.getUid());
                if (user == null) {
                    user = new User();
                    user.setUserId(userData.getUid());
                }
                user.setNickname(userData.getName());
                user.setAvatar(userData.getIcon());
                user.setGender(userData.getGender());
                if (user.isInserted()) {
                    DB.updateUser(user);
                } else {
                    DB.addUser(user);
                }
                callback.onSuccess(user);

                //同步联系人
                //todo:...

                //收上次未读消息
                if (response.getMessagesCount() > 0) {
                    for (chat.ono.chatsdk.proto.MessageProtos.Message msg : response.getMessagesList()) {
                        receiveMessage(msg, false);
                    }
                }
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                callback.onError(error.getCode(), error.getMessage());
            }
        });
    }

    public static void sendMessage(final chat.ono.chatsdk.model.Message message, String targetId, final ResultCallback<String> callback) {

        message.setMessageId(new ObjectId().toString());
        message.setTimestamp(System.currentTimeMillis());
        message.setSelf(true);
        message.setTargetId(targetId);
        message.setUser(IMCore.getInstance().getUser());
        message.setUserId(IMCore.getInstance().getUserId());

        DB.addMessage(message);

        Conversation conversation = getConversation(targetId);
        conversation.setUnreadCount(0);
        conversation.setContactTime(System.currentTimeMillis());
        conversation.setLastMessage(message);

        if (conversation.isInserted()) {
            DB.updateConversation(conversation);
        } else {
            DB.addConversation(conversation);
        }

        //send
        MessageProtos.SendMessageRequest request = MessageProtos.SendMessageRequest.newBuilder()
                .setTo(targetId)
                .setType(message.getType())
                .setData(message.encode())
                .setMid(message.getMessageId())
                .build();
        IMCore.getInstance().request("client.message.sendMessage", request, new Response() {
            @Override
            public void successResponse(Message msg) {
                message.setSend(true);
                DB.updateMessage(message);

                callback.onSuccess(message.getMessageId());
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                callback.onError(error.getCode(), error.getMessage());
            }
        });

    }
}
