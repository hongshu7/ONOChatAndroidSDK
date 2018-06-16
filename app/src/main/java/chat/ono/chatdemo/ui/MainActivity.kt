package chat.ono.chatdemo.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import chat.ono.chatdemo.R

import chat.ono.chatsdk.IMClient
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    protected var loginToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_avatar.setOnClickListener {
            var intent = Intent(this@MainActivity, TokensActivity::class.java)
            startActivityForResult(intent, 100)
        }

        btn_login.setOnClickListener {
            if (loginToken.isEmpty()) {
                Toast.makeText(this@MainActivity, "", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            connectToChat(loginToken)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            tv_name.text = data!!.getStringExtra("name")
            loginToken = data!!.getStringExtra("token")
            var avatar = data!!.getStringExtra("avatar")
            Glide.with(this).load(avatar).into(iv_avatar)
        }
    }
}
