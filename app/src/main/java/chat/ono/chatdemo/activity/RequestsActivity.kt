package chat.ono.chatdemo.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.RequestsAdapter
import chat.ono.chatdemo.view.LineDecoration
import chat.ono.chatsdk.IMClient
import kotlinx.android.synthetic.main.activity_requests.*

class RequestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        var adapter = RequestsAdapter(this)
        adapter.onButtonClick = {
            index, position ->
            var fr = adapter.get(position)
            if (index == 1) {
                IMClient.agreeFriend(fr.user.userId, {
                    Toast.makeText(this@RequestsActivity, "好友请求已同意~", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(this@RequestsActivity, it.message, Toast.LENGTH_LONG).show()
                })
            } else {
                IMClient.ignoreFriend(fr.user.userId, {
                    Toast.makeText(this@RequestsActivity, "好友请求已被忽略~", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(this@RequestsActivity, it.message, Toast.LENGTH_LONG).show()
                })
            }
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))

        IMClient.getFriendRequestList("", 30, {
            adapter.add(it)
            adapter.notifyDataSetChanged()
        }, {

        })
    }
}
