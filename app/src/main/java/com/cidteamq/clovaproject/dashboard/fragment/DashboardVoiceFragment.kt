package com.cidteamq.clovaproject.dashboard.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.dashboard.DashboardFragment
import com.cidteamq.clovaproject.dashboard.LearningInfo

class DashboardVoiceFragment : DashboardFragment {
    var info: LearningInfo? = null

    constructor(): super()
    constructor(info: LearningInfo): super(info) {
        this.info = info
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_voice, container, false)
        val wrapper = view!!.findViewById<LinearLayout>(R.id.voiceResultWrapper)
        val recogResult = info!!.voice
        for (i in 0 until recogResult.size) {
            val recogView = TextView(context)
            recogView.text = recogResult[i]
            recogView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            recogView.setTextColor(resources.getColor(R.color.black))
            recogView.gravity = Gravity.CENTER
            recogView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f)
            wrapper.addView(recogView)
        }
        return view
    }
}
