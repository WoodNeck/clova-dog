package com.cidteamq.clovaproject

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cidteamq.clovaproject.utils.RecyclerAdapter
import com.cidteamq.clovaproject.utils.Card
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        val items = arrayListOf<Card>(
                Card(R.drawable.ic_dashboard, "#1"),
                Card(R.drawable.ic_key, "#2"),
                Card(R.drawable.ic_mic, "#3"),
                Card(R.drawable.ic_paw_print, "#4"),
                Card(R.drawable.ic_thumb_down, "#5")
        )

        recyclerView.adapter = RecyclerAdapter(applicationContext, items, R.layout.activity_main)
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
