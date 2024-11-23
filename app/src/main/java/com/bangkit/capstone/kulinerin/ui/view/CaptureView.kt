package com.bangkit.capstone.kulinerin.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.bangkit.capstone.kulinerin.R
import kotlin.math.min

class CaptureView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private var isTouched = false

    constructor(context: Context): super(context) {
        paint.color = ContextCompat.getColor(context, R.color.white_blue)
        strokePaint.color = ContextCompat.getColor(context, R.color.white_blue)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        paint.color = ContextCompat.getColor(context, R.color.white_blue)
        strokePaint.color = ContextCompat.getColor(context, R.color.white_blue)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        paint.color = ContextCompat.getColor(context, R.color.white_blue)
        strokePaint.color = ContextCompat.getColor(context, R.color.white_blue)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.let {
            val x = width / 2f
            val y = height / 2f
            val radius = min(width, height) / 4f
            it.drawCircle(x, y, radius, paint)

            val strokeRadius = radius + 20f
            it.drawCircle(x, y, strokeRadius, strokePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouched = true
                paint.color = ContextCompat.getColor(context, R.color.light_blue)
                strokePaint.color = ContextCompat.getColor(context, R.color.light_blue)
                invalidate()
                performClick()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouched = false
                paint.color = ContextCompat.getColor(context, R.color.white_blue)
                strokePaint.color = ContextCompat.getColor(context, R.color.white_blue)
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}