package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.ono.chatdemo.R
import chat.ono.chatsdk.model.Conversation

class ConversationAdapter(var context: Context) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    var data:List<Conversation> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.view_item_conversation, parent)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
