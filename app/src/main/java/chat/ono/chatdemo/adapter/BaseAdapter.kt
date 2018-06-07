package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T:RecyclerView.ViewHolder, M>(var context: Context, var initData:List<M>? = null) : RecyclerView.Adapter<T>() {

    protected var data:ArrayList<M> = ArrayList()

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
    }

    fun add(conversation:M) {
        data.add(conversation)
    }

    fun add(items:List<M>) {
        data.addAll(items)
    }

    fun get(position: Int): M {
        return data[position]
    }

}
