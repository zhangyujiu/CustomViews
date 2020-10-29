package com.jk.custom.views

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.core.graphics.ColorUtils

class GradientAlphaTextSpan(var textColor: Int) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val originalColor = if (textColor == 0) paint.color else textColor
        val originalColorWithAlphaChanged = ColorUtils.setAlphaComponent(paint.color, 1)

        val textWidth = paint.measureText(text, start, end).toInt()

        val mShader = LinearGradient(
            x, 0F, x + textWidth - 10, 0F,
            originalColor, originalColorWithAlphaChanged, Shader.TileMode.CLAMP
        )
        paint.shader = mShader

        canvas.drawText(text ?: "", start, end, x, y.toFloat(), paint)

        paint.shader = null
    }

}