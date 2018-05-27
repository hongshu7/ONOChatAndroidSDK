ONO Chat SDK
=========
app 项目测试工程
chatsdk 聊天使用的SDK库

登录示例

    IMClient.setup("101.201.236.225", 3001);
    IMClient.connect("ju9es1b7w6kproa32ghqvdt0xzmfycin", new ResultCallback<User>() {
        @Override
        public void onSuccess(User result) {
            Log.v("chat", "login success with user:" + result.getNickname());
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Log.v("chat", "login failure with message:" + errorMessage) 
        }
    });
