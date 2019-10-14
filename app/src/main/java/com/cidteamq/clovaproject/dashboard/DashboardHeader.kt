package com.cidteamq.clovaproject.dashboard

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cidteamq.clovaproject.R

class DashboardHeader : LinearLayout {
    private var text: TextView? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_dashboard_header, this, false)
        addView(v)

        text = findViewById(R.id.fragmentTitle)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            val dashboard = (context as Activity).findViewById<DashboardView>(R.id.dashboard)
            dashboard.back()
        }
    }

    fun setHeader(title: String) {
        text?.text = title
    }
}
