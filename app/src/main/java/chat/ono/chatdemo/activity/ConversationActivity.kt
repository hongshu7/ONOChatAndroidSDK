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

    var adapter by Delegates.notNull<ConversationAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)



        adapter = ConversationAdapter(this)
        adapter.setOnItemClickListener {
            view, position ->
            var conversation = adapter.get(position)
            var intent = Intent(this@ConversationActivity, ChatActivity::class.java)
            intent.putExtra("target_id", conversation.targetId)
            startActivity(intent)
        }


        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))

    }

    override fun onResume() {
        super.onResume()
        var conversations = IMClient.getConversationList()
        adapter.clear()
        adapter.add(conversations)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.conversation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            var intent = Intent(this@ConversationActivity, ContactsActivity::class.java)
            intent.putExtra("showSearch", false)
            startActivity(intent)
        } else if (item.itemId == R.id.action_contacts) {
            var intent = Intent(this@ConversationActivity, ContactsActivity::class.java)
            intent.putExtra("showSearch", true)
            startActivity(intent)
        } else if (item.itemId == R.id.action_requests) {
            startActivity(Intent(this@ConversationActivity, RequestsActivity::class.java))
        } else if (item.itemId == R.id.action_logout) {
            var logout =  {
                startActivity(Intent(this@ConversationActivity, LoginActivity::class.java))
                finish()
            }
            IMClient.logout({

            }, {

            })
        }
        return super.onOptionsItemSelected(item)
    }
}
