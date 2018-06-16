package chat.ono.chatdemo.utils

import android.content.res.Resources

class ViewUtil {
    companion object {
        fun dps() : Int {
            return Resources.getSystem().displayMetrics.densityDpi
        }
        fun dp2px (dp : Int) : Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        fun px2dp (px : Int) : Int {
            return (px / Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}