package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import chat.ono.chatdemo.R
import chat.ono.chatsdk.model.FriendRequest
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.item_request.view.*

class RequestsAdapter(context: Context, data:ArrayList<FriendRequest>? = null) : BaseAdapter<RequestsAdapter.ItemViewHolder, FriendRequest>(context, data) {

    var onButtonClick:((Int, Int)->Unit)? = null

    override fun resId() = R.layout.item_request

    override fun onCreate(view: View): ItemViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBind(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.btn_agree.setOnClickListener {
                onButtonClick?.invoke(1, itemView.tag as Int)
            }
            itemView.btn_ignore.setOnClickListener {
                onButtonClick?.invoke(2, itemView.tag as Int)
            }
        }

        fun bind(fr: FriendRequest, position: Int) {
            Glide.with(itemView).load(fr.user.avatar).into(itemView.iv_avatar)
            itemView.tv_name.text = fr.user.nickname
            itemView.tv_greeting.text = fr.greeting
            itemView.tag = position
        }
    }
}
