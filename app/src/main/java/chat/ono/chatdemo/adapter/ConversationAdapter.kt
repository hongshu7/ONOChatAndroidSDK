package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.ono.chatdemo.R
import chat.ono.chatsdk.model.Conversation
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.view_item_conversation.view.*

class ConversationAdapter(var context: Context) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var data:ArrayList<Conversation> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.view_item_conversation, parent)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
    }

    fun add(conversation:Conversation) {
        data.add(conversation)
    }

    fun add(items:List<Conversation>) {
        data.addAll(items)
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(conversation: Conversation) {
            Glide.with(itemView).load(conversation.user.avatar).to(itemView.iv_avatar)
            itemView.tv_name.text = conversation.user.nickname
        }
    }
}
