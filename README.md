ONO Chat SDK
=========
app 项目测试工程  
chatsdk 聊天使用的SDK库

在Application的onCreated中执行：

    IMClient.init(this);

在需要登录的地方执行：

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

IMClient的方法说明：

