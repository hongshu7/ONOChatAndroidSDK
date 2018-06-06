package chat.ono.chatsdk.core;


import android.util.Log;

import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.net.SocketCallback;
import chat.ono.chatsdk.net.SocketManger;
import chat.ono.chatsdk.proto.MessageProtos;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by hansheng on 18/5/26.
 */
public class IMCore implements SocketCallback {
    private static IMCore instance;
    public static IMCore getInstance() {
        if (instance == null) {
            instance = new IMCore();
        }
        return instance;
    }

    private Map<String, RouteInfo> routes;
    private Map<Integer, String> routesById;
    private Map<Integer, ResponseInfo> responseMap;
    private Map<String, List<CallbackInfo>> pushMap;
    private int listenerId;

    private boolean isSetup;

    private String loginToken;
    private User user;
    private String userId;
    private String clientId;
    private Response loginCallback;

    private IMCore() {
        routes = new HashMap<>();
        routesById = new HashMap<>();
        responseMap = new HashMap<>();
        pushMap = new HashMap<>();
        listenerId = 0;
        SocketManger.getInstance().setCallback(this);
    }

    public RouteInfo getRouteInfo(String route) {
        return routes.get(route);
    }

    public String getRouteById(int routeId) {
        String route = routesById.get(routeId);
        return route == null ? "" : route;
    }

    public User getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public String getRouteByMsgId(int msgId) {
        ResponseInfo ri = responseMap.get(msgId);
        return ri == null ? "" : ri.getRoute();
    }

    public void setup(String host, int port) {
        SocketManger.getInstance().setup(host, port);
        isSetup = true;
    }

    public void connect() {
        if (!isSetup) {
            return;
        }
        SocketManger.getInstance().connect();
    }

    public void disconnect() {
        if (!isSetup) {
            return;
        }
        SocketManger.getInstance().disconnect();
    }

    public void uploadClientId() {
        if (clientId != null) {
//            MessageProtos.DeviceBindRequest request = MessageProtos.DeviceBindRequest.newBuilder()
//                    .setType(1) //client id为1
//                    .setToken(clientId)
//                    .build();
//            request("client.user.bindDevice", request, null);
            clientId = null;
        }
    }

    @Override
    public void handleMessage(Packet packet) {
        if (packet.getType() == Packet.TYPE_HANDSHAKE) {
            handleHandshake(packet.getStrings());
        } else if (packet.getType() == Packet.TYPE_DATA) {
            //处理response 及 push
            if (packet.getMessage() == null) return;
            if (packet.getMessage().getMessageId() > 0) {
                //response
                ResponseInfo ri = responseMap.get(packet.getMessage().getMessageId());
                if (ri != null) {
                    responseMap.remove(ri);
                    if (ri.getResponse() != null) {
                        if (packet.getMessage().isError()) {
                            ri.getResponse().errorResponse((MessageProtos.ErrorResponse) packet.getMessage().getMessage());
                        } else {
                            ri.getResponse().successResponse(packet.getMessage().getMessage());
                        }
                    }

                }
            } else {
                //push
                List<CallbackInfo> ris = pushMap.get(packet.getMessage().getRoute());
                if (ris != null) {
                    for (CallbackInfo ci : ris) {
                        ci.getCallback().callback(packet.getMessage().getMessage());
                    }
                }
            }
        }
    }

    public void handleHandshake(String data) {
        try {
            JSONObject info = new JSONObject(data);
            JSONObject sys = info.getJSONObject("sys");
            int heartbeat = sys.getInt("heartbeat");
            JSONObject routes = sys.getJSONObject("routes");
            Iterator<String> iter = routes.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = routes.getString(key);
                    String[] values = value.split(",");
                    RouteInfo routeInfo = new RouteInfo();
                    routeInfo.setRouteId(Integer.parseInt(values[0]));
                    if (values.length > 1 && !values[1].equals("") && !values[1].equals("_")) {
                        routeInfo.setRequest(values[1]);
                    }
                    if (values.length > 2 && !values[2].equals("") && !values[2].equals("_")) {
                        routeInfo.setResponse(values[2]);
                    }
                    this.routes.put(key, routeInfo);
                    this.routesById.put(routeInfo.getRouteId(), key);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            //开始心跳
            SocketManger.getInstance().startHeartbeat(heartbeat);
            //登录
            if (SocketManger.getInstance().isConnect()) {
                MessageProtos.UserLoginRequest request = MessageProtos.UserLoginRequest.newBuilder().setToken(this.loginToken).build();
                request("client.user.login", request, new Response() {
                    @Override
                    public void successResponse(com.google.protobuf.Message message) {
                        MessageProtos.UserData ud = ((MessageProtos.UserLoginResponse)message).getUser();
                        userId = ud.getUid();
                        User su = new User();
                        su.setUserId(ud.getUid());
                        su.setNickname(ud.getName());
                        su.setAvatar(ud.getAvatar());
                        su.setGender(ud.getGender());
                        user = su;
                        Log.v("IM", "login success:" + user.getNickname() +", id:" + user.getUserId());
                        uploadClientId();
                        if (loginCallback != null) {
                            loginCallback.successResponse(message);
                        }
                    }

                    @Override
                    public void errorResponse(MessageProtos.ErrorResponse error) {
                        if (loginCallback != null) {
                            loginCallback.errorResponse(error);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void request(String route, com.google.protobuf.Message message, Response response) {
        Random random = new Random(System.currentTimeMillis());

        Message msg = new Message();
        msg.setType(Message.TYPE_REQUEST);
        msg.setRoute(route);
        msg.setMessageId(random.nextInt(99999998) + 1);
        msg.setMessage(message);

        Packet packet = new Packet();
        packet.setType(Packet.TYPE_DATA);
        packet.setMessage(msg);


        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setRoute(route);
        responseInfo.setResponse(response);
        responseMap.put(msg.getMessageId(), responseInfo);

        SocketManger.getInstance().send(packet);
    }

    public void notify(String route, com.google.protobuf.Message message) {
        Message msg = new Message();
        msg.setType(Message.TYPE_NOTIFY);
        msg.setRoute(route);
        msg.setMessage(message);

        Packet packet = new Packet();
        packet.setType(Packet.TYPE_DATA);
        packet.setMessage(msg);
        SocketManger.getInstance().send(packet);
    }

    public int addPushListener(String route, IMCallback listener) {

        if (listener != null) {
            List<CallbackInfo> listeners = pushMap.get(route);
            if (listeners == null) {
                listeners = new ArrayList<>();
                pushMap.put(route, listeners);
            }
            CallbackInfo callbackInfo = new CallbackInfo();
            callbackInfo.setListenerId(++this.listenerId);
            callbackInfo.setCallback(listener);
            listeners.add(callbackInfo);
            return callbackInfo.getListenerId();
        } else {
            return 0;
        }

    }

    public void removePushListener(int listenerId) {

        for (String route : pushMap.keySet()) {
            List<CallbackInfo> listeners = pushMap.get(route);
            for (CallbackInfo callbackInfo : listeners) {
                if (callbackInfo.getListenerId() == listenerId) {
                    listeners.remove(callbackInfo);
                    return;
                }
            }
        }
    }


    public void login(String token, Response callback) {
        this.loginToken = token;
        this.loginCallback = callback;
        connect();
    }

    public void bindClientId(String clientId) {
        this.clientId = clientId;
        if (SocketManger.getInstance().isConnect()) {
            uploadClientId();
        }
    }

}
