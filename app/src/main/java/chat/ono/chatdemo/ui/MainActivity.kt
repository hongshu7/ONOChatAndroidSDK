package chat.ono.chatdemo.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import chat.ono.chatdemo.R

import chat.ono.chatsdk.IMClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login.setOnClickListener {
            connectToChat(et_token.text.toString())
        }
    }

    fun connectToChat(token: String) {
        btn_login.isEnabled = false
        IMClient.connect(token, {
            tv_msg.text =  "login success with user:${it.nickname}"
            Handler().postDelayed({
                startActivity(Intent(this@MainActivity, ConversationActivity::class.java))
                finish()
            }, 1000)
        },  {
            btn_login.isEnabled = true
            tv_msg.text =  "login failure with message:${it.message}"
        })

    }
}
