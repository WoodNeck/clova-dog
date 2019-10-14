package com.cidteamq.clovaproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.cidteamq.clovaproject.intro.IntroPage1
import com.cidteamq.clovaproject.intro.IntroPage2
import com.cidteamq.clovaproject.intro.LoginFragment
import com.facebook.CallbackManager
import me.relex.circleindicator.CircleIndicator
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class IntroActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var viewPager: ViewPager? = null
    private var circleIndicator: CircleIndicator? = null
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.viewPager)
        circleIndicator = findViewById(R.id.pageIndicator)

        val adapter = IntroPagerAdapter(supportFragmentManager)
        viewPager!!.adapter = adapter
        viewPager!!.currentItem = 0
        circleIndicator!!.setViewPager(viewPager)

        ActivityCompat.requestPermissions(this, permissions, 200)
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    inner class IntroPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 3

        override fun getItem(position: Int): Fragment? = when (position) {
            0 -> IntroPage1()
            1 -> IntroPage2()
            2 -> LoginFragment()
            else -> null
        }

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE
    }
}
