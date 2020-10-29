package com.jk.custom.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * 自定义ViewGroup中child绘制顺序
 */
class CustomOrderDrawChildLinearLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    override fun isChildrenDrawingOrderEnabled(): Boolean {
        return true
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return childCount - i - 1//倒序
    }
}