package com.cidteamq.clovaproject.modelInfo

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R

class ModelInfoHeader : LinearLayout {
    private var text: TextView? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_model_info_header, this, false)
        addView(v)

        text = findViewById(R.id.fragmentTitle_mi)
        val backBtn = findViewById<ImageView>(R.id.backBtn_mi)
        backBtn.setOnClickListener {
            (context as DogActivity).swapIntroductionArea()
        }


    }

    fun setHeader(title: String) {
        text?.text = title
    }
}
