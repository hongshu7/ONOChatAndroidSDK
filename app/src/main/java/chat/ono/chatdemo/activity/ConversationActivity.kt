package chat.ono.chatdemo.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ConversationAdapter
import chat.ono.chatdemo.view.LineDecoration
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.Conversation
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlin.properties.Delegates

class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        var conversations = IMClient.getConversationList()
        var adapter = ConversationAdapter(this)
        adapter.setOnItemClickListener {
            view, position ->
            var conversation = adapter.get(position)
            var intent = Intent(this@ConversationActivity, ChatActivity::class.java)
            intent.putExtra("target_id", conversation.targetId)
            startActivity(intent)
        }
        adapter.add(conversations)

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.conversation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            startActivity(Intent(this@ConversationActivity, ContactsActivity::class.java))
        } else if (item.itemId == R.id.action_requests) {
            startActivity(Intent(this@ConversationActivity, RequestsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
