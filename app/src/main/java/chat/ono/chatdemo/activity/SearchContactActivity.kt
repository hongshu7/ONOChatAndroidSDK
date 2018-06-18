package chat.ono.chatdemo.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.ContactsAdapter
import chat.ono.chatsdk.IMClient
import kotlinx.android.synthetic.main.activity_search_contact.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import chat.ono.chatdemo.view.LineDecoration


class SearchContactActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_contact)
        toolbar.setNavigationIcon(R.drawable.common_btn_back)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        var adapter = ContactsAdapter(this)
        adapter.setOnItemClickListener { view, position ->
            var user = adapter.get(position)
            showGreeting(user.nickname, user.userId)
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))

        btn_search.setOnClickListener {
            var keyword = et_keyword.text.toString()
            if (keyword.isEmpty()) {
                Toast.makeText(this@SearchContactActivity, "关键字不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            IMClient.searchFriends(keyword, {
                adapter.clear()
                adapter.add(it)
                //Log.v("IM", "user count:" + users.size)
                adapter.notifyDataSetChanged()
            }, {
                Toast.makeText(this@SearchContactActivity, it.message, Toast.LENGTH_LONG).show()
            })
        }



    }

    private fun showGreeting(name: String, userId: String) {
        val et = EditText(this)
        et.setText("你好～")
        AlertDialog.Builder(this).setTitle("与 $name 打招呼")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定") {
                    dialog: DialogInterface, which: Int ->
                        val input = et.text.toString()
                        if (input == "") {
                            Toast.makeText(applicationContext, "打招呼内容不能为空！$input", Toast.LENGTH_LONG).show()
                        } else {
                            IMClient.requestFriend(userId, input, {
                                Toast.makeText(this@SearchContactActivity, "好友请求已发送～", Toast.LENGTH_LONG).show()
                            }, {
                                Toast.makeText(this@SearchContactActivity, it.message, Toast.LENGTH_LONG).show()
                            })
                        }

                }
                .setNegativeButton("取消", null)
                .show()

    }
}
