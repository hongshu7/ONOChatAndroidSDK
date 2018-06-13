package chat.ono.chatdemo.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ContactsAdapter
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.User
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*
import kotlin.properties.Delegates

class ContactsActivity : AppCompatActivity() {

    var users by Delegates.notNull<List<User>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        toolbar.setNavigationIcon(R.drawable.common_btn_back)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        users = IMClient.getFriends()
        var adapter = ContactsAdapter(this)
        adapter.setOnItemClickListener {
            view, position ->
            var user = users[position]
            var intent = Intent(this@ContactsActivity, ChatActivity::class.java)
            intent.putExtra("target_id", user.userId)
            startActivity(intent)
        }

        adapter.add(users)

        rv_contacts.layoutManager = LinearLayoutManager(this)
        rv_contacts.adapter = adapter

        tb_tv_random.setOnClickListener {
            var userId = "test_uid_00" + (Random().nextInt(9) + 1).toString()
            IMClient.getRemoteUser(userId, {

            }, null)
            var intent = Intent(this@ContactsActivity, ChatActivity::class.java)
            intent.putExtra("target_id", userId)
            startActivity(intent)
            finish()
        }

    }
}