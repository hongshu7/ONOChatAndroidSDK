package chat.ono.chatdemo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ContactsAdapter
import chat.ono.chatsdk.IMClient
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        toolbar.setNavigationIcon(R.drawable.common_btn_back)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        var users = IMClient.getFriends()
        var adapter = ContactsAdapter(this)
        adapter.add(users)

        rv_contacts.layoutManager = LinearLayoutManager(this)
        rv_contacts.adapter = adapter


    }
}
