package com.jk.custom.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 * TextView using Spannable - ellipsize doesn't work
 * https://stackoverflow.com/questions/14691511/textview-using-spannable-ellipsize-doesnt-work
 */
class EllipsizeTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (lineCount > maxLines) {
            val lastLineEnd = layout.getLineEnd(maxLines - 1)
            val secondLastLineEnd = layout.getLineEnd(maxLines - 2)
            val charSequence: CharSequence

            val width = paint.measureText(text.subSequence(secondLastLineEnd, lastLineEnd).toString() + "...")
            if (width > layout.width) {
                val lastLineText = text.subSequence(secondLastLineEnd, lastLineEnd)
                for (i in lastLineText.indices) {
                    val cutWidth = paint.measureText(text.subSequence(secondLastLineEnd, lastLineEnd - i).toString() + "...")
                    if (cutWidth <= layout.width) {
                        charSequence = text.subSequence(0, lastLineEnd - i)
                        text = SpannableStringBuilder().append(charSequence).append("...")
                        break
                    }
                }
            } else {
                charSequence = text.subSequence(0, lastLineEnd)
                text = SpannableStringBuilder().append(charSequence).append("...")
            }
        }
    }

    /**
     * 阻止TextView移动
     * https://stackoverflow.com/questions/24027108/how-do-i-completely-prevent-a-textview-from-scrolling
     */
    override fun scrollTo(x: Int, y: Int) {
        //super.scrollTo(x, y)
        //do nothing
    }
}