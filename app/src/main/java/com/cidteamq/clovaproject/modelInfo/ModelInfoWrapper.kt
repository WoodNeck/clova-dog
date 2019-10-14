package com.cidteamq.clovaproject.modelInfo

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
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.modelInfo.fragment.ModelInfoGraph


class ModelInfoWrapper : RelativeLayout {
    var viewPager: ViewPager? = null
    var adapter: ModelInfoPagerAdapter? = null
    var header: ModelInfoHeader? = null


    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_model_info_content_wrapper, this, false)
        addView(v)

        viewPager = findViewById<ViewPager>(R.id.modelInfoViewPager)
        val indicator = findViewById<CircleIndicator>(R.id.modelInfoPageIndicator)
        adapter = ModelInfoPagerAdapter((context as AppCompatActivity).getSupportFragmentManager())
        viewPager?.adapter = adapter
        viewPager?.setCurrentItem(0)

        indicator.setViewPager(viewPager)

        header = findViewById(R.id.modelInfoHeader)
        val title = getResources().getString(R.string.model_info_list)
        header?.setHeader(title)
        setHeaderListener(header!!)


    }

    fun hide() {
        this.visibility = View.GONE
    }

    fun back(){
        (context as DogActivity).swapIntroductionArea()
    }

    fun setHeaderListener(header: ModelInfoHeader) {
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val stringId: Int
                when (position) {
                    0 -> stringId = R.string.model_info_overview
                    else -> stringId = 0
                }
                val title = getResources().getString(stringId)
                header.setHeader(title)
                Log.d("SELECTED", position.toString())
            }
        })
    }


    inner class ModelInfoPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 1

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            when (position) {
                0 -> return ModelInfoGraph()
                else -> return null
            }
        }

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

    }
}
