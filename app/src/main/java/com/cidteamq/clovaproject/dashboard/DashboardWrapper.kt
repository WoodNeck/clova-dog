package com.cidteamq.clovaproject.dashboard

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import me.relex.circleindicator.CircleIndicator
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.Log
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.dashboard.fragment.DashboardMorphemeFragment
import com.cidteamq.clovaproject.dashboard.fragment.DashboardOverviewFragment
import com.cidteamq.clovaproject.dashboard.fragment.DashboardResultFragment
import com.cidteamq.clovaproject.dashboard.fragment.DashboardVoiceFragment


class DashboardWrapper: RelativeLayout {
    var viewPager: ViewPager? = null
    var adapter: DashboardPagerAdapter? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_dashboard_content_wrapper, this, false)
        addView(v)

        viewPager = findViewById<ViewPager>(R.id.dashboardViewPager)
        val indicator = findViewById<CircleIndicator>(R.id.dashboardPageIndicator)
        adapter = DashboardPagerAdapter((context as AppCompatActivity).supportFragmentManager)
        viewPager?.adapter = adapter
        viewPager?.currentItem = 0

        indicator.setViewPager(viewPager)
    }

    fun showContent(info: LearningInfo, header: DashboardHeader) {
        this.visibility = View.VISIBLE
        viewPager?.setCurrentItem(0)
        adapter?.update(info)
        val title = getResources().getString(R.string.dashboard_overview)
        header.setHeader(title)
    }

    fun hide() {
        this.visibility = View.GONE
    }

    fun setHeaderListener(header: DashboardHeader) {
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val stringId: Int
                when (position) {
                    0 -> stringId = R.string.dashboard_overview
                    1 -> stringId = R.string.dashboard_voice
                    2 -> stringId = R.string.dashboard_morpheme
                    3 -> stringId = R.string.dashboard_result
                    else -> stringId = 0
                }
                val title = getResources().getString(stringId)
                header.setHeader(title)
                Log.d("SELECTED", position.toString())
            }
        })
    }

    inner class DashboardPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        var info: LearningInfo? = null

        override fun getCount(): Int = 4

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            when (position) {
                0 -> return DashboardOverviewFragment(info!!)
                1 -> return DashboardVoiceFragment(info!!)
                2 -> return DashboardMorphemeFragment(info!!)
                3 -> return DashboardResultFragment(info!!)
                else -> return null
            }
        }

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

        fun update(info: LearningInfo) {
            this.info = info
            notifyDataSetChanged()
        }
    }
}
