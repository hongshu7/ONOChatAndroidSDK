package chat.ono.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import chat.ono.chatsdk.IMClient;
import chat.ono.chatsdk.core.ResultCallback;
import chat.ono.chatsdk.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToChat();
    }

    public void connectToChat() {
        IMClient.setup("101.201.236.225", 3001);
        IMClient.connect("ju9es1b7w6kproa32ghqvdt0xzmfycin", new ResultCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.v("chat", "login success with user:" + result.getNickname());
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.v("chat", "login failure with message:" + errorMessage);
            }
        });
    }
}
