package com.cidteamq.clovaproject.dashboard.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.dashboard.DashboardFragment
import com.cidteamq.clovaproject.dashboard.LearningInfo
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class DashboardResultFragment : DashboardFragment {
    var info: LearningInfo? = null
    var chart: LineChart? = null

    constructor(): super()
    constructor(info: LearningInfo): super(info) {
        this.info = info
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_result, container, false)

        chart = view!!.findViewById(R.id.chart)
        chart!!.setTouchEnabled(false)
        initLineChart()
        setLineChartData(info!!.result, info!!.resultNext)

        val feedbackIcon = view.findViewById<ImageView>(R.id.feedbackResult)
        if (info!!.feedback == -1) {
            Log.d("TEST", info!!.feedback.toString())
            feedbackIcon.setImageResource(R.drawable.ic_thumb_down)
            feedbackIcon.setColorFilter(context!!.resources.getColor(R.color.cpb_red), android.graphics.PorterDuff.Mode.MULTIPLY)
        } else {
            feedbackIcon.setImageResource(R.drawable.ic_thumb_up)
            feedbackIcon.setColorFilter(context!!.resources.getColor(R.color.cpb_blue), android.graphics.PorterDuff.Mode.MULTIPLY)
        }

        return view
    }

    private fun initLineChart() {
        chart!!.description!!.isEnabled = false
        chart!!.setTouchEnabled(false)

        val leftAxis = chart!!.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 1.1f

        val rightAxis = chart!!.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val legend = chart!!.legend
        legend.yOffset = 10f
        legend.form = Legend.LegendForm.SQUARE
        legend.isEnabled = true

        val xAxis = chart!!.xAxis
        xAxis.textSize = 14f
        xAxis.setLabelCount(6, true)
        xAxis.yOffset = 10f
        xAxis.xOffset = 0f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 5f

        xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = IAxisValueFormatter { value, axis -> DogAction.toKoreanString(value.toInt()) }
        xAxis.textColor = Color.BLACK
    }

    private fun setLineChartData (result: FloatArray, resultNext: FloatArray) {
        val sets = ArrayList<ILineDataSet>()

        val yValsPrev = ArrayList<Entry>()
        val colorsPrev = ArrayList<Int>()
        for (i in 0 until result.size) {
            yValsPrev.add(Entry(i.toFloat(), result[i]))
            colorsPrev.add(Color.parseColor(DogAction.toThemeColor(i)))
        }

        val yValsNext = ArrayList<Entry>()
        val colorsNext = ArrayList<Int>()
        for (i in 0 until result.size) {
            yValsNext.add(Entry(i.toFloat(), resultNext[i]))
            colorsNext.add(Color.parseColor(DogAction.toDarkThemeColor(i)))
        }

        val dataSetPrev = LineDataSet(yValsPrev, "학습 전")
        dataSetPrev.lineWidth = 1.75f
        dataSetPrev.circleRadius = 5f
        dataSetPrev.circleHoleRadius = 2.5f
        dataSetPrev.color = context!!.resources.getColor(R.color.black)
        dataSetPrev.circleColors = colorsPrev
        dataSetPrev.setDrawIcons(false)
        //dataSetPrev.enableDashedLine(10f, 10f, 0f)

        val dataSetNext = LineDataSet(yValsNext, "학습 후")
        dataSetNext.lineWidth = 1.75f
        dataSetNext.circleRadius = 5f
        dataSetNext.circleHoleRadius = 2.5f
        dataSetNext.color = context!!.resources.getColor(R.color.cpb_red)
        dataSetNext.circleColors = colorsNext
        dataSetNext.setDrawIcons(false)

        sets.add(dataSetPrev)
        sets.add(dataSetNext)

        val data = LineData(sets)
        chart!!.data = data
    }
}
