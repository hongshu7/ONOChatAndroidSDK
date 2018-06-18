package chat.ono.chatdemo.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import chat.ono.chatdemo.R
import chat.ono.chatdemo.adapter.TokensAdapter
import chat.ono.chatdemo.model.TokenInfo
import chat.ono.chatdemo.view.LineDecoration
import chat.ono.chatsdk.IMClient
import chat.ono.chatsdk.model.User
import chat.ono.chatsdk.utils.FileHelper
import kotlinx.android.synthetic.main.activity_tokens.*
import kotlin.properties.Delegates

class TokensActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tokens)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }
        tb_tv_title.text = "选择用户"

        val tokenText = FileHelper.readAssetsFile(IMClient.getContext(), "tokens.txt")
        val tokens = tokenText.split(",".toRegex())
        var adapter = TokensAdapter(this)
        val avatarPrefix = "http://cdn.jingfu.org/a"
        for (i in 1..99) {
            var token = tokens[i-1]
            var tokenInfo = TokenInfo("test" + i.toString().padStart(3, '0'), avatarPrefix + i.toString().padStart(3, '0') + ".jpg", token)
            adapter.add(tokenInfo)
        }

        adapter.setOnItemClickListener {
            view, position ->
            var token = adapter.get(position)
            var intent = Intent()
            intent.putExtra("name", token.name)
            intent.putExtra("avatar", token.avatar)
            intent.putExtra("token", token.token)
            setResult(RESULT_OK, intent)
            finish()
        }


        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(LineDecoration(0, 0))


    }
}
