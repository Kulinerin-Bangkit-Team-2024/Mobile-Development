package com.bangkit.capstone.kulinerin.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.bangkit.capstone.kulinerin.R

class GalleryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val rectangle: RectF = RectF()

    private val cornerRadius = 10f


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rectangle.set(
            strokePaint.strokeWidth,
            strokePaint.strokeWidth,
            width - strokePaint.strokeWidth,
            height - strokePaint.strokeWidth
        )
        canvas.drawRoundRect(rectangle, cornerRadius, cornerRadius, strokePaint)
    }
}