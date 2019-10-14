package com.cidteamq.clovaproject

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater;
import android.view.View;
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import info.hoang8f.widget.FButton
import android.view.ViewGroup



class DogSelectionFragment : RelativeLayout {
    private var image: ImageView? = null
    private var dogNameTv: TextView? = null
    private var modelNameTv: TextView? = null
    private var dogDescTv: TextView? = null
    private var modelDescTv: TextView? = null
    private var selectBtn: FButton? = null
    private var dogName: String = ""
    private var descWrapper: LinearLayout? = null
    private var thisRow: LinearLayout? = null
    private var otherRow: LinearLayout? = null
    private var other: DogSelectionFragment? = null
    private var prevImageSize: Int = -1
    private var prevNameYPos: Int = -1
    private var currentAnimator: Animator? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_dog_selection, this, true)

        image = v.findViewById(R.id.dogImage)
        dogNameTv = v.findViewById(R.id.dogName)
        modelNameTv = v.findViewById(R.id.modelName)
        descWrapper = v.findViewById(R.id.descWrapper)
        dogDescTv = v.findViewById(R.id.dogDesc)
        modelDescTv = v.findViewById(R.id.modelDesc)
        selectBtn = v.findViewById(R.id.dogSelectBtn)

        val dogBtn = v.findViewById<RelativeLayout>(R.id.dogBtn)
        dogBtn.setOnClickListener {
            val activity = context as DogSelectionActivity
            if (activity.lastClicked == null) {
                val expander = makeExpandAnimator()
                currentAnimator = expander
                expander.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        otherRow!!.visibility = View.GONE
                        other!!.visibility = View.GONE

                        val descShower = makeDescShowAnimator()
                        currentAnimator = descShower
                        descShower.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                descWrapper!!.visibility = View.VISIBLE
                                selectBtn!!.visibility = View.VISIBLE
                                currentAnimator = null
                            }
                        })
                        descShower.start()
                    }
                })
                expander.start()
                activity.fragmentClicked(this)
            }
        }
        selectBtn!!.setOnClickListener {
            goToDogActivity()
        }
    }

    fun setResource(dogImage: Int, dogName: String) {
        image!!.setImageResource(dogImage)
        val realDogName = when (dogName) {
            "MLP" -> "Beagle"
            "RNN" -> "Corgi"
            "DQN" -> "Frenchie"
            "DRQN" -> "Pug"
            else -> ""
        }
        dogNameTv!!.text = realDogName
        modelNameTv!!.text = dogName
        this.dogName = dogName
    }

    fun setColor(color: String, colorBright: String, colorDark: String) {
        val dogBtn = findViewById<RelativeLayout>(R.id.dogBtn)
        dogBtn.setBackgroundColor(Color.parseColor(color))
        selectBtn!!.buttonColor = Color.parseColor(colorBright)
        selectBtn!!.shadowColor = Color.parseColor(colorDark)
    }

    fun setReference(thisRow: LinearLayout, other: DogSelectionFragment, otherRow: LinearLayout) {
        this.thisRow = thisRow
        this.other = other
        this.otherRow = otherRow
    }

    fun setDescription(dog: Int, model: Int) {
        dogDescTv!!.text = context.getString(dog)
        modelDescTv!!.text = context.getString(model)
    }

    fun toogleClickable() {
        val selectBtn = findViewById<RelativeLayout>(R.id.dogBtn)
        selectBtn.isClickable = !selectBtn.isClickable
    }

    fun shrink(): Boolean {
        if (currentAnimator != null) return false
        otherRow!!.visibility = View.VISIBLE
        other!!.visibility = View.VISIBLE
        descWrapper!!.visibility = View.GONE
        selectBtn!!.visibility = View.GONE

        val descHider = makeDescHideAnimator()
        descHider.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                image!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                val shrinker = makeShrinkAnimator()
                shrinker.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        descWrapper!!.visibility = View.GONE
                        selectBtn!!.visibility = View.GONE
                    }
                })
                shrinker.start()
            }
        })
        descHider.start()
        return true
    }

    private fun goToDogActivity() {
        val i = Intent(context, DogActivity::class.java)
        val activity = (context as DogSelectionActivity)
        i.putExtra("dogName", dogName)
        i.putExtra("userId", activity.userId)
        i.putExtra("userName", activity.userName)
        i.putExtra("userPic", activity.userPic)

        context.startActivity(i)
        activity.finish()
    }

    private fun makeExpandAnimator(): Animator {
        val expandAnimation = ValueAnimator.ofFloat(1f, 0f)
        expandAnimation.duration = 300
        expandAnimation.interpolator = AccelerateDecelerateInterpolator()
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            thisRow!!.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, fraction)
            this.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, fraction)
        }
        expandAnimation.addUpdateListener(updateListener)
        return expandAnimation
    }

    private fun makeShrinkAnimator(): Animator {
        val shrinkAnimation = ValueAnimator.ofFloat(0f, 1f)
        shrinkAnimation.duration = 300
        shrinkAnimation.interpolator = AccelerateDecelerateInterpolator()
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            thisRow!!.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, fraction)
            this.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, fraction)
        }
        shrinkAnimation.addUpdateListener(updateListener)
        return shrinkAnimation
    }

    private fun makeDescShowAnimator(): Animator {
        image!!.layoutParams.height = image!!.width

        val currentSize = image!!.height
        prevImageSize = currentSize
        val finalSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, resources.displayMetrics)
        val scale = finalSize / currentSize.toFloat()
        val descAnimation = ValueAnimator.ofFloat(1f, scale)
        descAnimation.duration = 500
        descAnimation.interpolator = AccelerateDecelerateInterpolator()
        var flag = false
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            image!!.layoutParams.height = (currentSize * fraction).toInt()
            image!!.requestLayout()
            if (!flag && animation.currentPlayTime > 140) {
                flag = true
                val param = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                param.addRule(RelativeLayout.BELOW, R.id.dogImage)
                val param2 = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                param2.addRule(RelativeLayout.BELOW, R.id.dogName)
                dogNameTv!!.layoutParams = param
                modelNameTv!!.layoutParams = param2
            }
        }
        descAnimation.addUpdateListener(updateListener)
        return descAnimation
    }

    private fun makeDescHideAnimator(): Animator {
        val currentSize = image!!.height
        val finalSize = prevImageSize
        val scale = finalSize / currentSize.toFloat()
        val descAnimation = ValueAnimator.ofFloat(1f, scale)
        descAnimation.duration = 500
        descAnimation.interpolator = AccelerateDecelerateInterpolator()
        var flag = false
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            image!!.layoutParams.height = (currentSize * fraction).toInt()
            image!!.requestLayout()
            if (!flag && animation.currentPlayTime > 340) {
                flag = true
                val param = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                param.addRule(RelativeLayout.ABOVE, R.id.footer)
                val param2 = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                param2.addRule(RelativeLayout.ABOVE, R.id.modelName)
                dogNameTv!!.layoutParams = param2
                modelNameTv!!.layoutParams = param
            }
        }
        descAnimation.addUpdateListener(updateListener)
        return descAnimation
    }
}
