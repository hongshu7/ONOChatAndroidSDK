package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import chat.ono.chatdemo.R
import chat.ono.chatsdk.model.User
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.item_contact.view.*

class ContactsAdapter(context: Context, data:ArrayList<User>? = null) : BaseAdapter<ContactsAdapter.ItemViewHolder, User>(context, data) {

    override fun resId() = R.layout.item_contact

    override fun onCreate(view: View): ItemViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBind(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            Glide.with(itemView).load(user.avatar).into(itemView.iv_avatar)
            itemView.tv_name.text = user.nickname
        }
    }
}
