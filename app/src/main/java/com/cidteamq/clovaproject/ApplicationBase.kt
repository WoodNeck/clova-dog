package com.cidteamq.clovaproject

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by pluvian on 12/7/17.
 */
class ApplicationBase: Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/NanumSquareRegular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build())
    }
}
