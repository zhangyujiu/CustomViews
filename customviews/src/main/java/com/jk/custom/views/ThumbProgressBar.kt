package com.jk.custom.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.jk.custom.R
import com.jk.custom.utils.DisplayUtils

class ThumbProgressBar : ProgressBar {
    private var mThumbDrawable: Drawable? = null

    //Thumb的宽度
    private var mThumbDrawableWidth = 0

    //Thumb的高度
    private var mThumbDrawableHeight = 0

    //进度条高度
    private var mProgressBarHeight = DisplayUtils.dip2px(context,12f)

    //Thumb偏移量
    private val mDefaultThumbOffset = DisplayUtils.dip2px(context,13f)

    //ProgressBar左右偏移量
    private val mProgressBarPadding = DisplayUtils.dip2px(context,16f)

    private var mPaint: Paint

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mThumbDrawable = ContextCompat.getDrawable(context, R.drawable.icon_package_check_rocket)
        mThumbDrawableWidth = mThumbDrawable?.intrinsicWidth ?: 0
        mThumbDrawableHeight = mThumbDrawable?.intrinsicHeight ?: 0
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dh = if (mThumbDrawableHeight > mProgressBarHeight) mThumbDrawableHeight else mProgressBarHeight
        setMeasuredDimension(
            View.getDefaultSize(0, widthMeasureSpec),
            dh
        )
        progressDrawable.setBounds(
            mProgressBarPadding,
            (mThumbDrawableHeight - mProgressBarHeight) / 2,
            measuredWidth - mProgressBarPadding,
            measuredHeight - (mThumbDrawableHeight - mProgressBarHeight) / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val percent = progress.toFloat() / max
        val progressWidth = measuredWidth - paddingLeft - paddingRight - mProgressBarPadding * 2
        var offsetX =
            progressWidth * percent + paddingLeft - mDefaultThumbOffset + mProgressBarPadding

        if (offsetX < 0) {
            offsetX = 0f
        }
        if (offsetX > measuredWidth - mThumbDrawableWidth) {
            offsetX = (measuredWidth - mThumbDrawableWidth).toFloat()
        }

        val bitmapDrawable = mThumbDrawable as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap
        canvas.drawBitmap(bitmap, offsetX, 0f, mPaint)
    }
}