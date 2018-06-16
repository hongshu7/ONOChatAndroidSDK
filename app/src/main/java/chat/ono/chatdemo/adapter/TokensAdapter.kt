package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import chat.ono.chatdemo.R
import chat.ono.chatdemo.model.TokenInfo
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.item_conversation.view.*

class TokensAdapter(context: Context, data:ArrayList<TokenInfo>? = null) : BaseAdapter<TokensAdapter.ItemViewHolder, TokenInfo>(context, data) {

    override fun resId() = R.layout.item_conversation

    override fun onCreate(view: View): ItemViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBind(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(token: TokenInfo) {
            Glide.with(itemView).load(token.avatar).into(itemView.iv_avatar)
            itemView.tv_name.text = token.name
            itemView.tv_msg.text = token.token
            itemView.tv_time.text = ""
        }
    }
}
