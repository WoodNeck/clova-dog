package com.cidteamq.clovaproject.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast

class BackBtnTimer(internal val context: Activity) {
    private var lastPressedTime: Long = 0
    private var toast: Toast? = null

    fun onBackPressed() {
        Log.d("TIMER", System.currentTimeMillis().toString())
        if (System.currentTimeMillis() > lastPressedTime + 2000) {
            lastPressedTime = System.currentTimeMillis()
            showGuide()
            return
        } else if (System.currentTimeMillis() <= lastPressedTime + 2000) {
            context.finish()
            toast?.cancel()
        }
    }

    private fun showGuide() {
        val toast = Toast.makeText(context, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
        toast.show()
    }
}
