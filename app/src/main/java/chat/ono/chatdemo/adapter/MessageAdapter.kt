package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import chat.ono.chatdemo.R
import chat.ono.chatsdk.model.Message
import chat.ono.chatsdk.model.TextMessage
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(context: Context, data:ArrayList<Message>? = null) : BaseAdapter<MessageAdapter.ItemViewHolder, Message>(context, data) {

    override fun resId() = R.layout.item_message

    override fun onCreate(view: View): ItemViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBind(vh: ItemViewHolder, position: Int) {
        vh.bind(data[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            itemView.tv_name.text = message.user.nickname
            if (message is TextMessage) {
                itemView.tv_msg.text = message.text
            }
        }
    }
}