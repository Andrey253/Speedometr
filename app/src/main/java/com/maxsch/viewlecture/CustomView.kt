package com.maxsch.viewlecture

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private companion object {
        const val VIEW_STATE_KEY = "state"
        const val SUPER_STATE = "super_state"
    }

    private var size = 320
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)



    private var bgColor: Int? = null
    private var borderColor = Color.BLACK
    private var borderWidth = 15.0f

    init {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.CustomView,
            defStyleAttr,
            defStyleRes
        )

        try {
            bgColor = typedArray.getColor(R.styleable.CustomView_colorBackground, Color.YELLOW)
        } finally {
            typedArray.recycle()
        }
    }





    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureDimension(size, widthMeasureSpec)
        val height = measureDimension(size, heightMeasureSpec)

        size = min(width, height)

        setMeasuredDimension(width, height)
    }

    private fun measureDimension(minSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> minSize.coerceAtMost(specSize)
            else -> minSize
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = bgColor!!
        paint.style = Paint.Style.FILL

        val radius = size / 2f

        canvas.drawCircle(size / 2f, size / 2f, radius, paint)

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth

        canvas.drawCircle(size / 2f, size / 2f, radius - borderWidth / 2f, paint)
    }
}