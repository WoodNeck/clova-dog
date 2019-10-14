package com.cidteamq.clovaproject.dashboard.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.dashboard.DashboardFragment
import com.cidteamq.clovaproject.dashboard.LearningInfo
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import me.grantland.widget.AutofitTextView
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class DashboardOverviewFragment : DashboardFragment {
    var info: LearningInfo? = null
    var chart: LineChart? = null

    constructor(): super()
    constructor(info: LearningInfo): super(info) {
        this.info = info
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_overview, container, false)

        val commandText = view!!.findViewById<TextView>(R.id.command)
        commandText!!.text = "\"${info!!.command}\""

        chart = view.findViewById(R.id.chart)
        initChart()
        setChartData(info!!.result)

        val actionIcon = view.findViewById<ImageView>(R.id.actionIcon)
        actionIcon.setImageResource(DogAction.toDrawable(info!!.action))

        val actionText = view.findViewById<TextView>(R.id.actionText)
        actionText.text = DogAction.toKoreanString(info!!.action)

        return view
    }

    private fun initChart() {
        chart!!.description!!.isEnabled = false
        chart!!.setTouchEnabled(false)

        val leftAxis = chart!!.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.isEnabled = false
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 1.1f

        val rightAxis = chart!!.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val legend = chart!!.legend
        legend?.isEnabled = false

        val xAxis = chart!!.xAxis
        xAxis.textSize = 8f
        xAxis.setLabelCount(6, true)
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 5f

        xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = IAxisValueFormatter { value, axis -> DogAction.toKoreanString(value.toInt()) }
        xAxis.textColor = Color.BLACK
    }

    private fun setChartData(probability: FloatArray) {
        val entryArray = ArrayList<Entry>()
        val colorArray = ArrayList<Int>()
        for (i in 0 until probability.size) {
            entryArray.add(Entry(i.toFloat(), probability[i]))
            colorArray.add(Color.parseColor(DogAction.toThemeColor(i)))
        }

        val dataSet = LineDataSet(entryArray, "")
        dataSet.circleColors = colorArray
        dataSet.color = context!!.resources.getColor(R.color.black)
        dataSet.lineWidth = 1.75f
        dataSet.circleRadius = 5f
        dataSet.circleHoleRadius = 2.5f

        val data = LineData(dataSet)
        data.setValueTextSize(8f)
        data.setDrawValues(true)
        data.setValueTextColor(Color.BLACK)

        chart!!.data = data
        chart!!.invalidate()
    }
}
