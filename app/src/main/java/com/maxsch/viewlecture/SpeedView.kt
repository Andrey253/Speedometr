package com.maxsch.viewlecture

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator

class SpeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
            defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var typeface: Typeface? = null
    private var maxValue = 120
    private var value = 0
    private var text = "km/h"
    private var color = -0x670050
    private var textColor = -0x6f5f01
    private var markRange = 10

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomView,
            defStyleAttr,
            defStyleRes
        )
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SpeedView)
            var chars = a.getText(R.styleable.SpeedView_android_text)
            text = chars?.toString() ?: "km/h"
            maxValue = a.getInt(R.styleable.SpeedView_maxValue, 120)
            value = a.getInt(R.styleable.SpeedView_value, 0)
            markRange = a.getInt(R.styleable.SpeedView_markRange, 10)
            color = a.getColor(R.styleable.SpeedView_color, -0x670050)
            textColor = a.getColor(R.styleable.SpeedView_textColor, -0x6f5f01)
            chars = a.getText(R.styleable.SpeedView_fontName)
            if (chars != null) {
                typeface = Typeface.createFromAsset(context.assets, chars.toString())
                paint.typeface = typeface
            }
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var width = width.toFloat()
        var height = height.toFloat()
        val aspect = width / height
        val normalAspect = 2f / 1f
        if (aspect > normalAspect) {
            width = normalAspect * height
        }
        if (aspect < normalAspect) {
            height = width / normalAspect
        }
        canvas.save()
        canvas.translate(width / 2, height)
        canvas.scale(.5f * width, -1f * height)
        paint.color = 0x40ffffff
        paint.style = Paint.Style.FILL
        paint.typeface = typeface
        canvas.drawCircle(0f, 0f, 1f, paint)
        paint.color = 0x20000000
        canvas.drawCircle(0f, 0f, 0.8f, paint)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.005f
        val scale = 0.9f
        val longScale = 0.9f
        val textPadding = 0.85f
        val step = Math.PI / maxValue
        for (i in 0..maxValue) {
            val x1 = Math.cos(Math.PI - step * i).toFloat()
            val y1 = Math.sin(Math.PI - step * i).toFloat()
            var x2: Float
            var y2: Float
            if (i % markRange == 0) {
                x2 = x1 * scale * longScale
                y2 = y1 * scale * longScale
            } else {
                x2 = x1 * scale
                y2 = y1 * scale
            }
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
        canvas.restore()
        canvas.save()
        canvas.translate(width / 2, 0f)
        paint.textSize = height / 10
        paint.color = textColor
        paint.style = Paint.Style.FILL
        val factor = height * scale * longScale * textPadding
        var i = 0
        while (i <= maxValue) {
            val x =
                Math.cos(Math.PI - step * i).toFloat() * factor
            val y =
                Math.sin(Math.PI - step * i).toFloat() * factor
            val text = Integer.toString(i)
            val textLen = Math.round(paint.measureText(text))
            canvas.drawText(Integer.toString(i), x - textLen / 2, height - y, paint)
            i += markRange
        }
        canvas.drawText(text, -paint.measureText(text) / 2, height - height * 0.15f, paint)
        canvas.restore()
        canvas.save()
        canvas.translate(width / 2, height)
        canvas.scale(.5f * width, -1f * height)
        canvas.rotate(90 - 180.toFloat() * (value / maxValue.toFloat()))
        paint.color = -0x7767
        paint.strokeWidth = 0.02f
        canvas.drawLine(0.01f, 0f, 0f, 1f, paint)
        canvas.drawLine(-0.01f, 0f, 0f, 1f, paint)
        paint.style = Paint.Style.FILL
        paint.color = -0x770067
        canvas.drawCircle(0f, 0f, .05f, paint)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val aspect = width / height.toFloat()
        val normalAspect = 2f / 1f
        if (aspect > normalAspect) {
            if (widthMode != MeasureSpec.EXACTLY) {
                width = Math.round(normalAspect * height)
            }
        }
        if (aspect < normalAspect) {
            if (heightMode != MeasureSpec.EXACTLY) {
                height = Math.round(width / normalAspect)
            }
        }
        setMeasuredDimension(width, height)
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        if (value > maxValue) {
            value = maxValue
        }
        invalidate()
    }

    fun setValue(value: Int) {
        this.value = Math.min(value, maxValue)
        invalidate()
    }

    var objectAnimator: ObjectAnimator? = null
    fun setValueAnimated(value: Int) {
        if (objectAnimator != null) {
            objectAnimator!!.cancel()
        }
        objectAnimator = ObjectAnimator.ofInt(this, "value", this.value, value)
        objectAnimator?.setDuration(100 + Math.abs(this.value - value) * 5.toLong())
        objectAnimator?.setInterpolator(DecelerateInterpolator())
        objectAnimator?.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newValue = getTouchValue(event.x, event.y)
                setValueAnimated(newValue)
                true
            }
            MotionEvent.ACTION_MOVE -> true
            MotionEvent.ACTION_UP -> true
            else -> super.onTouchEvent(event)
        }
    }

    private fun getTouchValue(x: Float, y: Float): Int {
        return if (x != 0f && y != 0f) {
            val startX = width / 2.toFloat()
            val startY = height.toFloat()
            val dirX = startX - x
            val dirY = startY - y
            val angle =
                Math.acos(dirX / Math.sqrt(dirX * dirX + dirY * dirY.toDouble())).toFloat()
            Math.round(maxValue * (angle / Math.PI.toFloat()))
        } else {
            value
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parentState = super.onSaveInstanceState()
        val savedState =
            SavedState(parentState)
        savedState.value = value
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState =
            state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        setValue(savedState.value)
    }

    private class SavedState : BaseSavedState {
        var value = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            value = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(value)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }
}