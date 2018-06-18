package chat.ono.chatdemo.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.MessageAdapter
import chat.ono.chatdemo.view.LineDecoration
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
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationIcon(R.drawable.common_btn_back)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        targetId = intent.getStringExtra("target_id")
        messages = IMClient.getMessageList(targetId, "", 20)
        var adapter = MessageAdapter(this)
        adapter.add(messages)

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))

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
