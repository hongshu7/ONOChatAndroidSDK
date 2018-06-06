package chat.ono.chatsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.Message;

import org.apache.commons.beanutils.BeanUtils;

import java.util.List;

import chat.ono.chatsdk.core.IMCore;
import chat.ono.chatsdk.core.Response;
import chat.ono.chatsdk.core.ResultCallback;
import chat.ono.chatsdk.model.TextMessage;
import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.proto.MessageProtos;
import chat.ono.chatsdk.utils.ObjectId;

/**
 * Created by kevin on 2018/5/27.
 */

public class IMClient {
    private IMClient instance;
    public IMClient getInstance() {
        if (instance == null) {
            instance = new IMClient();
        }
        return instance;
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

    public static String generateMessageId() {
        return ObjectId.get().toString();
    }

    public static chat.ono.chatsdk.model.Message createMessageFromType(int type) {

        switch (type) {
            case 1:
                return new TextMessage();
        }
        return null;
    }

    public static void setup(String host, int port) {
        IMCore.getInstance().setup(host, port);
    }

    public static void connect(String token, final ResultCallback<User> callback) {
        IMCore.getInstance().login(token, new Response() {
            @Override
            public void successResponse(Message message) {
                MessageProtos.UserData userData = ((MessageProtos.UserLoginResponse)message).getUser();
                User user = new User();
                user.setUserId(userData.getUid());
                user.setNickname(userData.getName());
                user.setAvatar(userData.getAvatar());
                user.setGender(userData.getGender());
                callback.onSuccess(user);
            }

            @Override
            public void errorResponse(MessageProtos.ErrorResponse error) {
                callback.onError(error.getCode(), error.getMessage());
            }
        });
    }
}
