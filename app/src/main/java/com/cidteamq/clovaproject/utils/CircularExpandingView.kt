package com.cidteamq.clovaproject.utils

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color
import android.graphics.Paint;
import android.graphics.PorterDuff
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

class CircularExpandingView : View {
    private val ANIMATION_DURATION = 800
    private val INTERPOLATOR: Interpolator = AccelerateDecelerateInterpolator()
    private var paintFormer: Paint? = null
    private var paintLatter: Paint? = null
    private var fraction: Float = 0f

    private var updateListener: ValueAnimator.AnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation -> setExpandFraction(animation.animatedValue as Float) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = canvas.width / 2
        val cy = canvas.height / 2
        val radiusFormer = Math.sqrt((cx * cx + cy * cy).toDouble()).toFloat() * fraction
        val radiusLatter = Math.sqrt((cx * cx + cy * cy).toDouble()).toFloat() * (fraction - 0.8f)

        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radiusFormer, paintFormer)
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radiusLatter, paintLatter)
    }

    fun setColor(colorFormer: Int, colorLatter: Int) {
        paintFormer!!.color = colorFormer
        paintLatter!!.color = colorLatter
    }

    fun expand(): Animator = animateExpandFraction(0.1f, 1.8f)

    fun contract(): Animator = animateExpandFraction(1f, 0.1f)

    fun reset() {
        fraction = 0f
    }

    fun setExpandFraction(expandFraction: Float) {
        this.fraction = expandFraction
        invalidate()
    }

    private fun initialize() {
        paintFormer = Paint()
        paintLatter = Paint()
    }

    private fun animateExpandFraction(from: Float, to: Float): Animator {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.setDuration(ANIMATION_DURATION.toLong())
        animator.setInterpolator(INTERPOLATOR)

        animator.addUpdateListener(updateListener)
        return animator
    }
}
