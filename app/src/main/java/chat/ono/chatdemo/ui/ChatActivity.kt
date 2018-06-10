package chat.ono.chatdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.MessageAdapter
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.Message
import chat.ono.chatsdk.model.TextMessage

import kotlinx.android.synthetic.main.activity_chat.*
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity() {

    var targetId by Delegates.notNull<String>()
    var messages by Delegates.notNull<MutableList<Message>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.common_btn_back)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        targetId = intent.getStringExtra("target_id")
        messages = IMClient.getMessageList(targetId, "", 20)
        var adapter = MessageAdapter(this)
        adapter.add(messages)

        rv_message.layoutManager = LinearLayoutManager(this)
        rv_message.adapter = adapter

        btn_send.setOnClickListener {
            var text = et_text.text.toString()
            if (text.isEmpty()) {
                return@setOnClickListener
            }
            et_text.text.clear()
            var message = TextMessage()
            message.text = text
            adapter.add(message)
            adapter.notifyDataSetChanged()

            IMClient.sendMessage(message, targetId, {

            }, {

            })
        }

    }

}
