package com.jk.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatSeekBar

/**
 * 自定义滑块顶部带进度显示的SeekBar
 */
class CustomSeekBar : AppCompatSeekBar {

    private var mProgressTextColor = Color.parseColor("#2496FF")
    private var mProgressTextSize = 14f
    private var mProgressTextPaddingBottom = 4f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar)
        mProgressTextColor =
            ta.getColor(R.styleable.CustomSeekBar_progressTextColor, mProgressTextColor)
        if (ta.hasValue(R.styleable.CustomSeekBar_progressTextSize)) {
            mProgressTextSize =
                ta.getDimension(R.styleable.CustomSeekBar_progressTextSize, mProgressTextSize)
            mProgressTextSize = DisplayUtils.px2sp(context, mProgressTextSize).toFloat()
        }
        if (ta.hasValue(R.styleable.CustomSeekBar_progressTextPaddingBottom)) {
            mProgressTextPaddingBottom = ta.getFloat(
                R.styleable.CustomSeekBar_progressTextPaddingBottom,
                mProgressTextPaddingBottom
            )
            mProgressTextPaddingBottom =
                DisplayUtils.dip2px(context, mProgressTextPaddingBottom).toFloat()
        }
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val thumbHeight = if (thumb == null) 0 else thumb.intrinsicHeight
        val measureText = measureText("0")
        val dh = thumbHeight + measureText.second + mProgressTextPaddingBottom
        setMeasuredDimension(
            View.getDefaultSize(0, widthMeasureSpec),
            View.resolveSizeAndState(dh.toInt(), heightMeasureSpec, 0)
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.save()
        //由于滑块和进度都是居中绘制，所以这里需要向下移动
        canvas?.translate(0f, mProgressTextPaddingBottom)
        super.onDraw(canvas)
        canvas?.restore()

        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = DisplayUtils.sp2px(context, mProgressTextSize).toFloat()
        paint.color = mProgressTextColor
        val textString = "${progress * 100 / max}%"
        val pair = measureText(textString)
        val percent = progress.toFloat() / max
        val progressWidth = width - paddingLeft - paddingRight
        val offsetX = progressWidth * percent + paddingLeft - pair.first / 2
        canvas?.drawText(
            textString,
            offsetX,
            pair.second.toFloat() - paint.fontMetrics.descent,
            paint
        )
    }

    private fun measureText(str: String): Pair<Int, Int> {
        val paint = Paint()
        paint.textSize = DisplayUtils.sp2px(context, mProgressTextSize).toFloat()
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        val w: Int = rect.width()
        val h: Int = rect.height()
        return Pair(w, h + paint.fontMetrics.descent.toInt())
    }
}