package chat.ono.chatdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.callback.FailureCallback
import chat.ono.chatsdk.callback.SuccessCallback
import chat.ono.chatsdk.model.User

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

    }
}
