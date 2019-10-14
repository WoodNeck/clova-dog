package com.cidteamq.clovaproject.interact.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.cidteamq.clovaproject.utils.CircularExpandingView
import com.cidteamq.clovaproject.interact.InteractAreaLayout
import com.cidteamq.clovaproject.interact.InteractAreaState
import com.cidteamq.clovaproject.interact.InteractView
import android.support.v4.content.res.ResourcesCompat
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.cidteamq.clovaproject.R


class InteractIdleView : InteractView {
    private var recordButton: LinearLayout? = null
    private var circularExpandingView: CircularExpandingView? = null
    private var SHRINK_DURATION: Long = 400

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.tag = InteractAreaState.State.IDLE

        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_interact_idle, this, false)
        addView(v)

        circularExpandingView = CircularExpandingView(context)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        circularExpandingView!!.visibility = View.GONE
        addView(circularExpandingView, params)

        recordButton = v.findViewById(R.id.btnRecord)
        recordButton!!.setOnClickListener({
            prepareForAnimation()
            startAnimation()
        })
        recordButton!!.isClickable = false
    }

    fun makeAvailable() {
        val btnImage = findViewById<ImageView>(R.id.btnImage)
        btnImage.setImageResource(R.drawable.ic_mic)
        recordButton!!.isClickable = true
    }

    fun makeUnavailable() {
        val btnImage = findViewById<ImageView>(R.id.btnImage)
        btnImage.setImageResource(R.drawable.ic_mic_off)
        recordButton!!.isClickable = false
    }

    private fun prepareForAnimation(){
        val buttonLayout = recordButton as LinearLayout
        val recordIcon = buttonLayout.getChildAt(0)
        recordIcon?.visibility = View.GONE

        val colorFormer = ResourcesCompat.getColor(resources, R.color.idleMicBtnBackground, null)
        val colorLatter = ResourcesCompat.getColor(resources, R.color.interactVoiceBackground, null)
        circularExpandingView?.setColor(colorFormer, colorLatter)
        circularExpandingView?.visibility = View.VISIBLE
    }

    private fun startAnimation(){
        val shrinkAnimator = makeShrinkAnimator()
        val expandAnimator = circularExpandingView?.expand()
        val parent = parent as InteractAreaLayout
        val view = this

        val buttonLayout = recordButton as LinearLayout
        val buttonParams = buttonLayout.layoutParams
        val prevWidth = buttonParams.width
        val prevHeight = buttonParams.height

        shrinkAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                expandAnimator?.start()
                recordButton?.visibility = View.GONE
                buttonParams.width = prevWidth
                buttonParams.height = prevHeight
                buttonLayout.layoutParams = buttonParams
            }
        })

        expandAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                parent.swapView(view, parent.getView(InteractAreaState.State.VOICE))
                circularExpandingView?.reset()
                circularExpandingView?.visibility = View.GONE
            }
        })

        shrinkAnimator.start()
    }

    override fun hide() {
        this.visibility = View.GONE
    }

    override fun show() {
        this.visibility = View.VISIBLE
        val buttonLayout = recordButton as LinearLayout
        buttonLayout.visibility = View.VISIBLE
        val recordIcon = buttonLayout.getChildAt(0)
        recordButton?.visibility = View.VISIBLE
        recordIcon?.visibility = View.VISIBLE
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    private fun makeShrinkAnimator(): Animator{
        val buttonLayout = recordButton as LinearLayout
        val buttonParams = buttonLayout.layoutParams
        val prevWidth = buttonParams.width
        val prevHeight = buttonParams.height

        val shrinkAnimator = ValueAnimator.ofFloat(1f, 0f)
        shrinkAnimator.duration = SHRINK_DURATION
        shrinkAnimator.interpolator = AccelerateDecelerateInterpolator()
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            buttonParams.width = (prevWidth * fraction).toInt()
            buttonParams.height = (prevHeight * fraction).toInt()
            buttonLayout.layoutParams = buttonParams
        }
        shrinkAnimator.addUpdateListener(updateListener)
        return shrinkAnimator
    }
}
