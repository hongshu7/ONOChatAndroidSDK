package chat.ono.chatdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.User

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectToChat()
    }

    fun connectToChat() {
        IMClient.setup("101.201.236.225", 3001)
        IMClient.connect("ju9es1b7w6kproa32ghqvdt0xzmfycin", {
            Log.v("chat", "login success with user:${it.nickname}")
        },  {
            Log.v("chat", "login failure with message:${it.message}")
        })

    }
}
