package chat.ono.chatdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.LayoutInflater
import android.view.ViewGroup
import chat.ono.chatdemo.R


abstract class BaseAdapter<T:RecyclerView.ViewHolder, M>(var context: Context, var initData:List<M>? = null) : RecyclerView.Adapter<T>(), View.OnClickListener {


    private var onItemClickListener: ((View, Int) -> Unit)? = null
    protected var data:ArrayList<M> = ArrayList()

    abstract fun resId(): Int

    abstract fun onCreate(view: View) : T

    abstract fun onBind(vh: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        var view = LayoutInflater.from(context).inflate(resId(), parent, false)
        view.setOnClickListener(this)
        return onCreate(view)
    }

    override fun onBindViewHolder(vh: T, position: Int) {
        vh.itemView.tag = position
        onBind(vh, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
    }

    fun add(item: M) {
        data.add(item)
    }

    fun add(items:List<M>) {
        data.addAll(items)
    }

    fun get(position: Int): M {
        return data[position]
    }

    fun setOnItemClickListener(listener: (View, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onClick(view: View) {
        onItemClickListener?.invoke(view, view.tag as Int)
    }

}
