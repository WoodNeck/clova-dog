package com.cidteamq.clovaproject.dashboard.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.dashboard.DashboardFragment
import com.cidteamq.clovaproject.dashboard.LearningInfo
import com.nex3z.flowlayout.FlowLayout

class DashboardMorphemeFragment : DashboardFragment {
    var info: LearningInfo? = null

    constructor(): super()
    constructor(info: LearningInfo): super(info) {
        this.info = info
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_morpheme, container, false)
        view?.findViewById<TextView>(R.id.command)?.text = info!!.command
        val morphemeWrapper = view?.findViewById<FlowLayout>(R.id.morphemeWrapper)
        val lparams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        for (morpheme in info!!.morpheme) {
            val morphemeView = TextView(context)
            morphemeView.layoutParams = lparams
            val paddingPixel = 20
            val density = context!!.resources.displayMetrics.density
            val paddingDp = (paddingPixel * density).toInt()
            morphemeView.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            morphemeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            morphemeView.text = morpheme
            morphemeView.setTextColor(Color.BLACK)
            morphemeView.setBackgroundResource(R.drawable.shape_morpheme_bubble)
            morphemeWrapper?.addView(morphemeView)
        }

        return view
    }
}
