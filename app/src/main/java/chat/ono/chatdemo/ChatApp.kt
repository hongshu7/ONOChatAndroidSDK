package chat.ono.chatdemo

import android.app.Application
import chat.ono.chatsdk.IMClient

class ChatApp : Application() {
    override fun onCreate() {
        super.onCreate()

        IMClient.init(this)
    }
}