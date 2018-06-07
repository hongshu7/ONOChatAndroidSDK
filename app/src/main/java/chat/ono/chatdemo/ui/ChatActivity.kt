package chat.ono.chatdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import chat.ono.chatdemo.R

import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)


    }

}
