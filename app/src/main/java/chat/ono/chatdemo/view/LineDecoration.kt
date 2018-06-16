package chat.ono.chatdemo.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View
import chat.ono.chatdemo.utils.ViewUtil


/**
 * Created by kevin on 2018/2/23.
 */
class LineDecoration(var itemRes: Int = 0, var offset: Int = 30, var borderColor: String = "#dddddd") : RecyclerView.ItemDecoration() {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mItemSize = ViewUtil.dp2px(1)
    private var drawLast =  false
    init {
        mPaint.style = Paint.Style.FILL
    }

    fun borderColor(bc: String): LineDecoration {
        borderColor = bc
        return this
    }

    fun drawLast(dl: Boolean): LineDecoration {
        drawLast = dl
        return this
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: State) {
        mPaint.color = Color.parseColor(borderColor)
        val left = parent.paddingLeft + ViewUtil.dp2px(offset)
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            val adapter = parent.adapter
            val itemType = adapter.getItemViewType(position)
            if ((itemRes == 0 || itemType == itemRes) && position < adapter.itemCount - (if (drawLast) 0 else 1)) {
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + layoutParams.bottomMargin
                val bottom = top + mItemSize
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, recyclerView: RecyclerView, state: State) {
        val position = recyclerView.getChildAdapterPosition(view)
        val adapter = recyclerView.adapter
        val itemType = adapter.getItemViewType(position)
        if (itemType == itemRes && position < adapter.itemCount - 1) {
            outRect.set(0, 0, 0, mItemSize)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }
}