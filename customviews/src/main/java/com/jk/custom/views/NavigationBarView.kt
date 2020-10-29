package com.jk.custom.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.jk.custom.utils.DisplayUtils

/**
 * 导航栏占位 View
 */
class NavigationBarView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            0
        } else {
            DisplayUtils.retrieveNavigationHeight(context)
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }
}
