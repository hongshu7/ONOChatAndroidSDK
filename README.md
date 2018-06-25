ONO Chat SDK
=========
app 项目测试工程  
chatsdk 聊天使用的SDK库

在Application的onCreated中执行：

```java
IMClient.init(this);
```

在需要登录的地方执行：

```java
IMClient.connect("ju9es1b7w6kproa32ghqvdt0xzmfycin", new SuccessCallback<User>() {
    @Override
    public void onSuccess(User result) {
        Log.v("chat", "login success with user:" + result.getNickname());
    }
}, new FailureCallback() {
    @Override
    public void onError(ErrorInfo error) {
        Log.v("chat", "login failure with message:" + error.getMessage()) 
    }
});
```

IMClient的方法说明：

```java
/**
 * 登录聊天服务器，使用服务端生成的token来登录聊天服务器
 *
 * @param token            登录用的token，此token由服务端接口生成
 * @param successCallback  成功回调，带参数user
 * @param failureCallback  失败回调
 */
public static void connect(String token, final SuccessCallback<User> successCallback, final FailureCallback failureCallback);

/**
 * 退出登录
 *
 * @param successCallback  成功回调
 * @param failureCallback  失败回调
 */
public static void logout(final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);

/**
 * 发送一条消息给联系人
 *
 * @param message          要发送的消息实体
 * @param targetId         发送的对象ID
 * @param successCallback  成功回调，带参数消息
 * @param failureCallback  失败回调
 */
public static void sendMessage(final Message message, String targetId, final SuccessCallback<Message> successCallback, final FailureCallback failureCallback);

/**
 * 获得会话列表
 *
 * @return 会话列表
 */
public static List<Conversation> getConversationList();

/**
 * 获取单个会话
 *
 * @param tagetId  对象ID
 * @return 会话
 */
public static Conversation getConversation(String tagetId);

/**
 * 获取好友列表
 *
 * @return 好友列表
 */
public static List<User> getFriends();

 /**
 * 获取当前未读消息数
 *
 * @return 未读消息数
 */
public static int getTotalUnreadCount();

/**
 * 获取用户信息
 *
 * @param userId 用户ID
 * @return 用户信息
 */
public static User getUser(String userId);

/**
 * 获取远程用户信息
 *
 * @param userId 用户ID
 * @param successCallback 成功回调，带参数user
 * @param failureCallback  失败回调
 */
public static void getRemoteUser(String userId, final SuccessCallback<User> successCallback, final FailureCallback failureCallback);

/**
 * 获取单条信息
 *
 * @param messageId 消息ID
 * @return 消息实体
 */
public static Message getMessage(String messageId);

/**
 * 获取消息列表
 *
 * @param targetId  对象ID
 * @param offfset   获取起始ID
 * @param limit     获取数量
 * @return
 */
public static List<Message> getMessageList(String targetId, String offfset, int limit);

/**
 * 根据用户昵称查找用户列表
 *
 * @param keyword    关键字
 * @param successCallback 成功回调，参数是用户列表
 * @param failureCallback 失败回调
 */
public static void searchFriends(String keyword, final SuccessCallback<List<User>> successCallback, final FailureCallback failureCallback);

/**
 * 请求好友
 *
 * @param userId  请求的用户ID
 * @param greeting  打招呼内容
 * @param successCallback 成功回调
 * @param failureCallback 失败回调
 */
public static void requestFriend(String userId, String greeting, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);

/**
 * 获取好友请求列表
 *
 * @param offset  分页起始ID
 * @param limit   获取数量
 * @param successCallback 成功回调，参数是请求列表
 * @param failureCallback 失败回调
 */
public static void getFriendRequestList(String offset, int limit, final SuccessCallback<List<FriendRequest>> successCallback, final FailureCallback failureCallback);

/**
 * 同意好友请求
 *
 * @param userId  要同意的用户ID
 * @param successCallback  成功回调
 * @param failureCallback  失败回调
 */
public static void agreeFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);

/**
 * 忽略好友请求
 *
 * @param userId  要忽略的用户ID
 * @param successCallback  成功回调
 * @param failureCallback  失败回调
 */
public static void ignoreFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);

/**
 * 删除好友
 * @param userId 要删除的好友ID
 * @param successCallback  成功回调
 * @param failureCallback  失败回调
 */
public static void deleteFriend(String userId, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);

/**
 * 备注好友
 *
 * @param userId  好友ID
 * @param remark  备注名称
 * @param successCallback  成功回调
 * @param failureCallback  失败回调
 */
public static void remarkFriend(String userId, String remark, final SuccessEmptyCallback successCallback, final FailureCallback failureCallback);
```
