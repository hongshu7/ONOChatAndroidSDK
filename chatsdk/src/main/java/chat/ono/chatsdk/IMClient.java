package chat.ono.chatsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import chat.ono.chatsdk.callback.ErrorInfo;
import chat.ono.chatsdk.callback.SuccessEmptyCallback;
import chat.ono.chatsdk.core.DB;
import chat.ono.chatsdk.core.IMCallback;
import chat.ono.chatsdk.core.IMCore;
import chat.ono.chatsdk.callback.MessageCallback;
import chat.ono.chatsdk.core.Response;
import chat.ono.chatsdk.callback.SuccessCallback;
import chat.ono.chatsdk.callback.FailureCallback;
import chat.ono.chatsdk.model.AudioMessage;
import chat.ono.chatsdk.model.Conversation;
import chat.ono.chatsdk.model.FriendRequest;
import chat.ono.chatsdk.model.ImageMessage;
import chat.ono.chatsdk.model.Message;
import chat.ono.chatsdk.model.SmileMessage;
import chat.ono.chatsdk.model.TextMessage;
import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.net.SocketManger;
import chat.ono.chatsdk.proto.MessageProtos;
import chat.ono.chatsdk.utils.ObjectId;

/**
 * Created by kevin on 2018/5/27.
 */

public class IMClient {
    public static class Options {
        public String host;
        public int port;
        public Options(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    private static Context context;
    private static Options options;

    static {
        options = new Options("ono-chat.340wan.com", 3000);
        Log.v("IMClient", "IMClient init");
        IMCore.getInstance().addPushListener("push.message", new IMCallback() {
            @Override
            public void callback(com.google.protobuf.Message message) {
                receiveMessage((MessageProtos.Message)message, true);
            }
        });
        IMCore.getInstance().addPushListener("push.newFriend", new IMCallback() {
            @Override
            public void callback(com.google.protobuf.Message message) {
                receiveNewFriend((MessageProtos.NewFriend)message);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    public static void init(Context ctx) {
        context = ctx;
    }

    public static void init(Context ctx, Options opts) {
        context = ctx;
        options = opts;
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

    private static void receiveMessage(MessageProtos.Message msg, boolean dispatch) {
        Message message = createMessageFromType(msg.getType());

        User sendUser = DB.fetchUser(message.getUserId());
        message.setMessageId(msg.getMid());
        message.setTimestamp(System.currentTimeMillis());
        message.setSend(true);
        message.setSelf(IMCore.getInstance().getUserId().equals(msg.getFrom()));
        String targetId = message.isSelf() ? msg.getTo() : msg.getFrom();
        message.setTargetId(targetId);
        //message.setUser(IMCore.getInstance().getUser());
        message.setUserId(msg.getFrom());
        message.setUser(sendUser);
        message.decode(msg.getData());

        //save message
        DB.addMessage(message);

        //update conversion
        Conversation conversation = getOrCreateConversation(targetId);
        conversation.setUser(sendUser);
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
            messageCallback.onMessageReceived(message);
        }

    }

    private static void receiveNewFriend(MessageProtos.NewFriend nf) {
        User friend = convertUserFromMessage(nf.getUser());
        DB.addUser(friend);
        DB.addFriend(friend.getUserId());
        Log.v("IM", "push & add friend user:" + friend.getUserId());
        //todo: update friendsUpdateTime
        if (messageCallback != null) {
            messageCallback.onNewFriend(friend);
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

    public static Message createMessageFromType(int type) {

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
            Log.i("IM", "conversation null for targetId:" + targetId);
            conversation = new Conversation();
            conversation.setConversationType(conversationType);
            conversation.setTargetId(targetId);
        }
        return conversation;
    }


    public static void connect(String token, final SuccessCallback<User> successCallback, final FailureCallback failureCallback) {
        IMCore.getInstance().login(options.host, options.port, token, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
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
                MessageProtos.FriendOperations friendOperations = response.getFriendOperations();
                if (friendOperations != null) {
                    if (friendOperations.getAddsCount() > 0) {
                        for (MessageProtos.UserData ud : friendOperations.getAddsList()) {
                            User um = convertUserFromMessage(ud);
                            Log.v("IM", "add friend user:" + um.getUserId());
                            DB.addUser(um);
                            DB.addFriend(um.getUserId());
                        }
                    }
                    if (friendOperations.getDeletesCount() > 0) {
                        for (String userId : friendOperations.getDeletesList()) {
                            DB.deleteFriend(userId);
                        }
                    }
                    if (friendOperations.getUpdatesCount() > 0) {
                        //todo: update
                    }
                    //todo: update friendsUpdateTime
                }

                //收上次未读消息
                if (response.getMessagesCount() > 0) {
                    for (MessageProtos.Message msg : response.getMessagesList()) {
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

    public static void logout(final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {

        IMCore.getInstance().request("im.user.logout", null, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                SocketManger.getInstance().disconnect();
                if (successCallback != null) {
                    successCallback.onSuccess();
                }
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                SocketManger.getInstance().disconnect();
                if (failureCallback != null) {
                    failureCallback.onError(ErrorInfo.fromMessage(error));
                }
            }
        });
    }

    public static void sendMessage(final Message message, String targetId, final SuccessCallback<Message> successCallback, final FailureCallback failureCallback) {

        message.setMessageId(generateMessageId());
        message.setTimestamp(System.currentTimeMillis());
        message.setSelf(true);
        message.setTargetId(targetId);
        message.setUser(IMCore.getInstance().getUser());
        message.setUserId(IMCore.getInstance().getUserId());

        DB.addMessage(message);

        final Conversation conversation = getOrCreateConversation(targetId);
        conversation.setUnreadCount(0);
        conversation.setContactTime(System.currentTimeMillis());
        conversation.setUser(DB.fetchUser(targetId));
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
        IMCore.getInstance().request("im.message.send", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message msg) {
                MessageProtos.SendMessagenResponse smr = (MessageProtos.SendMessagenResponse)msg;
                message.setMessageId(smr.getNmid());
                message.setSend(true);
                conversation.setLastMessageId(smr.getNmid());
                DB.updateMessage(message, smr.getOmid());
                DB.updateConversation(conversation);
                if (successCallback != null) {
                    successCallback.onSuccess(message);
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
        List<Conversation> conversations =  DB.fetchConversations();
        for (Conversation conversation : conversations) {
            if (conversation.getConversationType() == Conversation.ConversationType.Private) {
                conversation.setUser(DB.fetchUser(conversation.getTargetId()));
            } else {
                //todo: fill group
            }
            conversation.setLastMessage(DB.fetchMessage(conversation.getLastMessageId()));
        }
        return conversations;
    }

    public static Conversation getConversation(String tagetId) {
        Conversation conversation = DB.fetchConversation(tagetId);
        if (conversation != null) {
            if (conversation.getConversationType() == Conversation.ConversationType.Private) {
                conversation.setUser(DB.fetchUser(conversation.getTargetId()));
            } else {
                //todo: fill group
            }
            conversation.setLastMessage(DB.fetchMessage(conversation.getLastMessageId()));
        }
        return conversation;
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
            public void successResponse(com.google.protobuf.Message message) {
                MessageProtos.UserProfileResponse upr = (MessageProtos.UserProfileResponse)message;
                User user = convertUserFromMessage(upr.getUser());
                DB.addUser(user); //save to local
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

    public static Message getMessage(String messageId) {
        Message message = DB.fetchMessage(messageId);
        if (message != null) {
            message.setUser(DB.fetchUser(message.getUserId()));
        }
        return message;
    }

    public static List<Message> getMessageList(String targetId, String offfset, int limit) {
        List<Message> messages = DB.fetchMessages(targetId, offfset, limit);
        User selfUser = IMCore.getInstance().getUser();
        User targetUser = DB.fetchUser(targetId);
        for (Message message: messages) {
            message.setUser(message.isSelf() ? selfUser : targetUser);
        }
        return messages;
    }

    public static void searchFriends(String keyword, final SuccessCallback<List<User>> successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendSearchRequest request = MessageProtos.FriendSearchRequest.newBuilder().setKeyword(keyword).build();
        IMCore.getInstance().request("im.friend.search", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                List<User> users = new ArrayList<>();
                if (message != null) {
                    MessageProtos.FriendSearchResponse response = (MessageProtos.FriendSearchResponse) message;
                    for (MessageProtos.UserData ud : response.getUsersList()) {
                        User user = convertUserFromMessage(ud);
                        users.add(user);
                    }
                }
                if (successCallback != null) {
                    successCallback.onSuccess(users);
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

    public static void requestFriend(String userId, String greeting, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendRequestRequest request = MessageProtos.FriendRequestRequest.newBuilder().setUid(userId).setGreeting(greeting).build();
        IMCore.getInstance().request("im.friend.request", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                if (successCallback != null) {
                    successCallback.onSuccess();
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

    public static void getFriendRequestList(String offset, int limit, final SuccessCallback<List<FriendRequest>> successCallback, final FailureCallback failureCallback) {
        if (offset == null || offset.equals("")) {
            offset = System.currentTimeMillis() + "";
        }
        final MessageProtos.FriendRequestListRequest request = MessageProtos.FriendRequestListRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .build();
        IMCore.getInstance().request("im.friend.requests", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                List<FriendRequest> requests = new ArrayList<>();
                if (message != null) {
                    MessageProtos.FriendRequestListResponse response = (MessageProtos.FriendRequestListResponse) message;
                    for (MessageProtos.NewFriendRequest nfr : response.getRequestListList()) {
                        FriendRequest fr = new FriendRequest();
                        fr.setUser(convertUserFromMessage(nfr.getUser()));
                        fr.setGreeting(nfr.getGreeting());
                        requests.add(fr);
                    }
                }
                if (successCallback != null) {
                    successCallback.onSuccess(requests);
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

    public static void agreeFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendAgreeRequest request = MessageProtos.FriendAgreeRequest.newBuilder().setUid(userId).build();
        IMCore.getInstance().request("im.friend.agree", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                if (successCallback != null) {
                    successCallback.onSuccess();
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

    public static void ignoreFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendIgnoreRequest request = MessageProtos.FriendIgnoreRequest.newBuilder().setUid(userId).build();
        IMCore.getInstance().request("im.friend.ignore", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                if (successCallback != null) {
                    successCallback.onSuccess();
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

    public static void deleteFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendDeleteRequest request = MessageProtos.FriendDeleteRequest.newBuilder().setUid(userId).build();
        IMCore.getInstance().request("im.friend.delete", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                if (successCallback != null) {
                    successCallback.onSuccess();
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

    public static void remarkFriend(String userId, String remark, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback) {
        final MessageProtos.FriendRemarkRequest request = MessageProtos.FriendRemarkRequest.newBuilder().setUid(userId).setRemark(remark).build();
        IMCore.getInstance().request("im.friend.remark", request, new Response() {
            @Override
            public void successResponse(com.google.protobuf.Message message) {
                if (successCallback != null) {
                    successCallback.onSuccess();
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
