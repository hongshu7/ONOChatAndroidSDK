package chat.ono.chatdemo.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ContactsAdapter
import chat.ono.chatdemo.view.LineDecoration
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.User
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlin.properties.Delegates

class ContactsActivity : AppCompatActivity() {

    var adapter by Delegates.notNull<ContactsAdapter>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }


        adapter = ContactsAdapter(this)
        adapter.setOnItemClickListener {
            view, position ->
            var user = adapter.get(position)
            var intent = Intent(this@ContactsActivity, ChatActivity::class.java)
            intent.putExtra("target_id", user.userId)
            startActivity(intent)
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))

        var showSearch = intent.getBooleanExtra("showSearch", false)
        if (!showSearch) {
            tb_tv_title.text = "新建会话"
            tb_tv_search.visibility = View.GONE
        }
        tb_tv_search.setOnClickListener {
            var intent = Intent(this@ContactsActivity, SearchContactActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        adapter.clear()
        var users = IMClient.getFriends()
        adapter.add(users)
        adapter.notifyDataSetChanged()
    }
}
