package chat.ono.chatdemo.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ConversationAdapter
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.Conversation
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlin.properties.Delegates

class ConversationActivity : AppCompatActivity() {
    var conversations by Delegates.notNull<List<Conversation>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        conversations = IMClient.getConversationList()
        var adapter = ConversationAdapter(this)
        adapter.setOnItemClickListener {
            view, position ->
            var conversation = conversations[position]
            var intent = Intent(this@ConversationActivity, ChatActivity::class.java)
            intent.putExtra("target_id", conversation.targetId)
            startActivity(intent)
        }
        adapter.add(conversations)

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter

        tb_tv_add.setOnClickListener {
            startActivity(Intent(this@ConversationActivity, ContactsActivity::class.java))
        }

    }
}
