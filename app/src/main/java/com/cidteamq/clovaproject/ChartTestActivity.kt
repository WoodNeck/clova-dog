package com.cidteamq.clovaproject

import android.content.Context
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity
import android.graphics.Color;
import android.view.WindowManager;
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry

import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class ChartTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_test)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        val mpChart: LineChart = findViewById<LineChart>(R.id.mpChart);
        mpChart.setBackgroundColor(Color.WHITE);
        mpChart.setGridBackgroundColor(Color.argb(150, 51, 181, 229));
        mpChart.setDrawGridBackground(true);
        mpChart.setDrawBorders(true);
        mpChart.getDescription().setEnabled(false);
        mpChart.setPinchZoom(false);
        mpChart.getAxisRight().setEnabled(false);
        mpChart.getLegend().setEnabled(false)

        val leftAxis = mpChart.getAxisLeft()
        leftAxis?.setAxisMaximum(900f)
        leftAxis?.setAxisMinimum(-250f)
        leftAxis?.setDrawAxisLine(false)
        leftAxis?.setDrawZeroLine(false)
        leftAxis?.setDrawGridLines(false)

        setMpData(100, 60f, mpChart)

        mpChart.invalidate();
    }

    fun setMpData(count: Int, range: Float, mpChart: LineChart) {
        val yVals1 = ArrayList<Entry>()

        for (i in 0..count - 1) {
            val value: Float = (Math.random() * range).toFloat() + 50f
            yVals1.add(Entry(i.toFloat(), value))
        }

        val yVals2 = ArrayList<Entry>()

        for (i in 0..count - 1) {
            val value: Float = (Math.random() * range).toFloat() + 450f
            yVals2.add(Entry(i.toFloat(), value))
        }

        val set1: LineDataSet?
        val set2: LineDataSet?

        if (mpChart.getData() != null && mpChart.getData().getDataSetCount() > 0) {
            set1 = mpChart.getData().getDataSetByIndex(0) as LineDataSet?
            set2 = mpChart.getData().getDataSetByIndex(1) as LineDataSet?
            set1?.values = yVals1
            set2?.values = yVals2
            mpChart.getData().notifyDataChanged()
            mpChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(yVals1, "DataSet 1")

            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = Color.rgb(255, 241, 46)
            set1.setDrawCircles(false)
            set1.lineWidth = 2f
            set1.circleRadius = 3f
            set1.fillAlpha = 255
            set1.setDrawFilled(true)
            set1.fillColor = Color.WHITE
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> mpChart.getAxisLeft().getAxisMinimum() }

            // create a dataset and give it a type
            set2 = LineDataSet(yVals2, "DataSet 2")
            set2.axisDependency = YAxis.AxisDependency.LEFT
            set2.color = Color.rgb(255, 241, 46)
            set2.setDrawCircles(false)
            set2.lineWidth = 2f
            set2.circleRadius = 3f
            set2.fillAlpha = 255
            set2.setDrawFilled(true)
            set2.fillColor = Color.WHITE
            set2.setDrawCircleHole(false)
            set2.highLightColor = Color.rgb(244, 117, 117)
            set2.fillFormatter = IFillFormatter { dataSet, dataProvider -> mpChart.getAxisLeft().getAxisMaximum() }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets
            dataSets.add(set2)

            // create a data object with the datasets
            val data = LineData(dataSets)
            data.setDrawValues(false)

            // set data
            mpChart.setData(data)
        }
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
