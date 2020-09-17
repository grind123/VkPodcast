package com.grind.vkpodcasts.utils

import android.content.res.Resources

class PixelsUtils {
    companion object{
        fun dpToPx(dp: Int): Int{
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        fun pxToDp(px: Int): Int{
            return (px / Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}