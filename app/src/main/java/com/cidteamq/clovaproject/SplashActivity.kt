package com.cidteamq.clovaproject

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class SplashActivity : AppCompatActivity() {
    private val SHOW_DURATION: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val activity = this
        val task = object: AsyncTask<String, String, String>() {
            override fun doInBackground(vararg p0: String?): String {
                Thread.sleep(SHOW_DURATION)
                val intent = Intent(activity, IntroActivity::class.java)
                startActivity(intent)
                finish()
                return "0"
            }
        }
        task.execute()
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
