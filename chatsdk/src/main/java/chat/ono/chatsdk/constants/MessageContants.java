package chat.ono.chatsdk.constants;

public class MessageContants {

    //七牛图片地址
    public static final String MESSAGE_IMG_URL = "http://7xki9o.com2.z0.glb.qiniucdn.com/";
    //七牛声音地址
    public static final String MESSAGE_VOICE_URL = "http://7xki9k.com2.z0.glb.qiniucdn.com/";

    public static final int IMVersion = 1;  //第四版聊天协议

    public static final int MessageTypeText = 1;
    public static final int MessageTypeImage = 2;
    public static final int MessageTypeVoice = 3;
    public static final int MessageTypeRedpacket = 4;
    public static final int MessageTypeTip = 5;
    public static final int MessageTypeBeFriend = 6;
    public static final int MessageInfoTypeUpToken = 1;

    /**
     * 选择服务器
     */
    public static final String QUERY_SERVER = "gate.entry.query";

    /**
     * 登录聊天系统
     */
    public static final String LOGIN_CHAT_SERVER = "connector.entry.login";

    /**
     * 补充用户信息
     */
    public static final String ON_NEEDINFO = "on.needInfo";

    /**
     * 新消息
     */
    public static final String ON_MESSAGE = "on.msgs";

    /**
     * 群组新加人进来：
     */
    public static final String ON_GROUP_USERADD = "on.group.userAdd";

    /**
     * 群组信息变更：
     */
    public static final String ON_GROUP_CHANGE = "on.group.change";

    /**
     * 群组人员退出
     */
    public static final String ON_GROUP_USERQUIT = "on.group.userQuit";

    /**
     * 群信息
     */
    public static final String ON_GROUP_MSGS = "on.group.msgs";

    /**
     * 新消息至已读
     */
    public static final String SET_MESSAGE_READ = "chat.message.read";

    /**
     * 发送消息
     */
    public static final String SEND_MESSAGE = "chat.message.send";

    /**
     * 获取七牛token
     */
    public static final String GET_QINIU_TOKEN = "chat.system.qiniu";

    /**
     * 获取用户信息
     */
    public static final String GET_USER_INFO = "chat.user.profile";

    /**
     * 获取多个用户信息
     */
    public static final String GET_USERS_INFO = "chat.user.query";

    /**
     * 获取群信息
     */
    public static final String GET_GROUP_INFO = "chat.group.info";

    /**
     * 更改用户信息
     */
    public static final String UPDATE_USER_INFO = "chat.user.update";

    /**
     * 进入群聊
     */
    public static final String ENTER_GROUP_CHAT = "chat.group.enter";

    /**
     * 发送群消息
     */
    public static final String SEND_GROUP_MESSAGE = "chat.group.send";

    /**
     * 自己加入群组消息
     */
    public static final String ON_GROUP_JOIN = "on.group.join";

    /**
     * 获取群组成员
     */
    public static final String GET_GROUP_MEMBERS = "chat.group.members";

    /**
     * 群消息至已读
     */
    public static final String SET_GROUP_MESSAGE_READ = "chat.group.read";
}
