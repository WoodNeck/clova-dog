package com.cidteamq.clovaproject.interact.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.interact.InteractAreaLayout
import com.cidteamq.clovaproject.interact.InteractAreaState
import com.cidteamq.clovaproject.interact.InteractView
import com.github.mikephil.charting.charts.RadarChart
import org.json.JSONObject
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.animation.Easing

class InteractResultView : InteractView {
    private var textDisplay: TextView? = null
    private var spiderChart: RadarChart? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.tag = InteractAreaState.State.RESULT

        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_interact_result, this, true)

        textDisplay = v.findViewById(R.id.resultText)

        spiderChart = v.findViewById(R.id.spiderChart)
        spiderChart!!.setTouchEnabled(false)
        initSpiderChart()
    }

    override fun hide() {
        this.visibility = View.GONE
    }

    override fun show() {
        this.visibility = View.VISIBLE
        val parent = parent as InteractAreaLayout
        val resultView = this
        val feedbackView = parent.getView(InteractAreaState.State.FEEDBACK) as InteractFeedbackView

        val animation = ValueAnimator.ofFloat(0.1f, 1f)
        animation.duration = 3000
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                parent.swapView(resultView, feedbackView)
            }
        })
        animation.start()
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    fun setResult(resultObj: JSONObject) {
        val finalText = resultObj.getString("command")
        setText(finalText)
        val probJson = resultObj.getJSONArray("probability")
        val probability = FloatArray(probJson.length())
        for (i in 0 until probJson.length()) {
            probability[i] = probJson.getDouble(i).toFloat()
        }
        setSpiderChartData(probability)

        val parent = parent as InteractAreaLayout
        val feedbackView = parent.getView(InteractAreaState.State.FEEDBACK) as InteractFeedbackView
        feedbackView.setResult(resultObj)

        val action = resultObj.getInt("action")
        (context as DogActivity).changePose(DogAction.toActionString(action))
    }

    private fun setText(text: String) {
        textDisplay?.text = text
    }

    private fun initSpiderChart() {
        spiderChart?.description?.isEnabled = false
        spiderChart?.webLineWidth = 1f
        spiderChart?.webColor = Color.LTGRAY
        spiderChart?.webLineWidthInner = 1f
        spiderChart?.webColorInner = Color.LTGRAY
        spiderChart?.webAlpha = 100

        val yAxis = spiderChart?.yAxis
        yAxis?.setDrawLabels(false)

        val legend = spiderChart?.legend
        legend?.isEnabled = false
    }

    private fun setSpiderChartData(probability: FloatArray) {
        val entries = ArrayList<RadarEntry>()
        probability.mapTo(entries) { RadarEntry(it) }

        val dataSet = RadarDataSet(entries, "")
        dataSet.color = Color.rgb(103, 110, 129)
        dataSet.fillColor = Color.rgb(103, 110, 129)
        dataSet.setDrawFilled(false)
        dataSet.fillAlpha = 180
        dataSet.lineWidth = 2f
        dataSet.isDrawHighlightCircleEnabled = false
        dataSet.setDrawHighlightIndicators(false)

        val data = RadarData(dataSet)
        data.setValueTextSize(8f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.WHITE)

        spiderChart!!.data = data
        spiderChart!!.invalidate()

        spiderChart!!.animateXY(
            1400, 1400,
            Easing.EasingOption.EaseInOutQuad,
            Easing.EasingOption.EaseInOutQuad)

        val xAxis = spiderChart!!.xAxis
        xAxis.textSize = 9f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase): String =
                DogAction.toKoreanString(value.toInt())
        }
        xAxis.textColor = Color.WHITE
    }
}
