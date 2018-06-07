package chat.ono.chatsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.Message;

import java.util.List;

import chat.ono.chatsdk.callback.ErrorInfo;
import chat.ono.chatsdk.core.DB;
import chat.ono.chatsdk.core.IMCallback;
import chat.ono.chatsdk.core.IMCore;
import chat.ono.chatsdk.core.MessageCallback;
import chat.ono.chatsdk.core.Response;
import chat.ono.chatsdk.callback.SuccessCallback;
import chat.ono.chatsdk.callback.FailureCallback;
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
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
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
        Conversation conversation = getOrCreateConversation(targetId);
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

    public static User convertUserFromMessage(MessageProtos.UserData userData) {
        User user = new User();
        user.setUserId(userData.getUid());
        user.setNickname(userData.getName());
        user.setAvatar(userData.getAvatar());
        user.setGender(userData.getGender());
        return user;
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

    private static Conversation getOrCreateConversation(String targetId) {
        return getOrCreateConversation(targetId, Conversation.ConversationType.Private);
    }

    private static Conversation getOrCreateConversation(String targetId, int conversationType) {
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

    public static void connect(String token, final SuccessCallback<User> successCallback, final FailureCallback failureCallback) {
        IMCore.getInstance().login(token, new Response() {
            @Override
            public void successResponse(Message message) {
                MessageProtos.UserLoginResponse response = (MessageProtos.UserLoginResponse)message;
                MessageProtos.UserData userData = response.getUser();
                //获取自身信息
                User user = DB.fetchUser(userData.getUid());
                if (user == null) {
                    user = convertUserFromMessage(userData);
                    DB.addUser(user);
                } else {
                    user.setNickname(userData.getName());
                    user.setAvatar(userData.getAvatar());
                    user.setGender(userData.getGender());
                    DB.updateUser(user);
                }
                if (successCallback != null) {
                    successCallback.onSuccess(user);
                }

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
                if (failureCallback != null) {
                    failureCallback.onError(ErrorInfo.fromMessage(error));
                }
            }
        });
    }

    public static void sendMessage(final chat.ono.chatsdk.model.Message message, String targetId, final SuccessCallback<String> successCallback, final FailureCallback failureCallback) {

        message.setMessageId(generateMessageId());
        message.setTimestamp(System.currentTimeMillis());
        message.setSelf(true);
        message.setTargetId(targetId);
        message.setUser(IMCore.getInstance().getUser());
        message.setUserId(IMCore.getInstance().getUserId());

        DB.addMessage(message);

        Conversation conversation = getOrCreateConversation(targetId);
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
        IMCore.getInstance().request("client.message.send", request, new Response() {
            @Override
            public void successResponse(Message msg) {
                message.setSend(true);
                DB.updateMessage(message);

                if (successCallback != null) {
                    successCallback.onSuccess(message.getMessageId());
                }
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                if (failureCallback != null) {
                    failureCallback.onError(ErrorInfo.fromMessage(error));
                }
            }
        });

    }

    public static List<Conversation> getConversationList() {
        return DB.fetchConversations();
    }

    public static Conversation getConversation(String tagetId) {
        return DB.fetchConversation(tagetId);
    }

    public static List<User> getFriends() {
        return DB.fetchFriends();
    }

    public static int getTotalUnreadCount() {
        return DB.getTotalUnreadCount();
    }

    public static User getUser(String userId) {
        return DB.fetchUser(userId);
    }

    public static void getRemoteUser(String userId, final SuccessCallback<User> successCallback, final FailureCallback failureCallback) {
        MessageProtos.UserProfileRequest request = MessageProtos.UserProfileRequest.newBuilder()
                .setUid(userId)
                .build();
        IMCore.getInstance().request("im.user.profile", request, new Response() {
            @Override
            public void successResponse(Message message) {
                MessageProtos.UserData userData = (MessageProtos.UserData)message;
                User user = convertUserFromMessage(userData);
                if (successCallback != null) {
                    successCallback.onSuccess(user);
                }
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                if (failureCallback != null) {
                    failureCallback.onError(ErrorInfo.fromMessage(error));
                }
            }
        });

    }
}
