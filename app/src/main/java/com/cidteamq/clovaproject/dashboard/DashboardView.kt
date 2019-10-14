package com.cidteamq.clovaproject.dashboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class DashboardView : InfoView {
    var header: DashboardHeader? = null
    private var uncheckedLogs: Int = 0
    private var dogActivity: DogActivity? = null
    private var cardWrapper: RecyclerView? = null
    private var contentWrapper: DashboardWrapper? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_dashboard_list, this, false)
        addView(v)

        dogActivity = context as DogActivity
        val websocket = dogActivity!!.listener
        websocket.addHandler(WebSocketHandler(this))

        val layoutManager = LinearLayoutManager(context)
        cardWrapper = findViewById(R.id.cardWrapper)
        cardWrapper?.setHasFixedSize(true)
        cardWrapper?.layoutManager = layoutManager

        contentWrapper = findViewById(R.id.contentWrapper)

        val cardAdapter = LearningCardAdapter(context)
        cardWrapper?.adapter = cardAdapter

        header = findViewById(R.id.dashboardHeader)
        val title = getResources().getString(R.string.dashboard_list)
        header?.setHeader(title)
        contentWrapper?.setHeaderListener(header!!)
    }

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.GONE
    }

    fun showCard() {
        cardWrapper?.visibility = View.VISIBLE
        contentWrapper?.hide()
        val title = getResources().getString(R.string.dashboard_list)
        header?.setHeader(title)
    }

    fun showContent(info: LearningInfo) {
        cardWrapper?.visibility = View.GONE
        contentWrapper?.showContent(info, header!!)
        Log.d("INFO", info.command)
    }

    fun back() {
        if (isShowingCard()) {
            (context as DogActivity).swapDashboardArea()
        } else {
            showCard()
        }
    }

    fun resetLogCounter() {
        uncheckedLogs = 0
    }

    private fun addLog(item: LearningInfo) {
        val adapter = cardWrapper?.adapter as LearningCardAdapter
        val items = adapter.items.clone() as ArrayList<LearningInfo>
        items.add(0, item)
        if (items.size > adapter.ITEM_MAX_SIZE) {
            items.removeAt(adapter.ITEM_MAX_SIZE)
        }
        updateLog(items)
        uncheckedLogs += 1
        dogActivity!!.logCountUpdate(uncheckedLogs)
    }

    private fun updateLog(items: ArrayList<LearningInfo>) {
        val cardAdapter = LearningCardAdapter(context)
        cardAdapter.items = items
        cardWrapper?.adapter = cardAdapter
    }

    private fun serverOnMessage(msg: Message) {
        when (msg.what) {
            DogWebSocketListener.MessageType.INIT.ordinal -> {
                val items = ArrayList<LearningInfo>()
                val obj = JSONObject(msg.obj as String)
                val logs = obj.getJSONArray("logs")

                for (i in 0 until logs.length()) {
                    val log = logs.getJSONObject(i)
                    val recognition = getStringArrayFromLog(log, "recognition", context.getString(R.string.voice_not_exist))
                    val morpheme = getStringArrayFromLog(log, "words", context.getString(R.string.morpheme_not_exist))
                    val probability = getFloatArrayFromLog(log, "probabilities")
                    val probabilityNext = getFloatArrayFromLog(log, "next_probabilities")
                    val model = log.getString("dog_name")
                    val action = log.getInt("action")
                    val info = LearningInfo(
                        action,
                        DogAction.toDrawable(action),
                        log.getString("created_time"),
                        log.getString("command"),
                        model,
                        recognition,
                        morpheme,
                        probability,
                        probabilityNext,
                        log.getDouble("feedback").toInt()
                    )
                    Log.d("TEST2", log.getDouble("feedback").toString() + "/" + log.getDouble("feedback").toInt() + "/" + info.feedback)
                    items.add(info)
                }
                updateLog(items)
            }
            DogWebSocketListener.MessageType.LEARN.ordinal -> {
                val obj = JSONObject(msg.obj as String)
                val log = obj.getJSONObject("log")
                val recognition = getStringArrayFromLog(log, "recognition", context.getString(R.string.voice_not_exist))
                val morpheme = getStringArrayFromLog(log, "words", context.getString(R.string.morpheme_not_exist))
                val probability = getFloatArrayFromLog(log, "probabilities")
                val probabilityNext = getFloatArrayFromLog(log, "next_probabilities")
                val model = log.getString("dog_name")
                val action = log.getInt("action")
                val info = LearningInfo(
                    action,
                    DogAction.toDrawable(action),
                    log.getString("created_time"),
                    log.getString("command"),
                    model,
                    recognition,
                    morpheme,
                    probability,
                    probabilityNext,
                    log.getDouble("feedback").toInt()
                )
                addLog(info)
            }
        }
    }

    private fun getStringArrayFromLog(log: JSONObject, key: String, errorMsg: String): ArrayList<String> {
        val result = ArrayList<String>()
        try {
            val array = log.getJSONArray(key)
            for (i in 0 until array.length()) {
                result.add(array.getString(i))
            }
        } catch (e: JSONException) {
            result.add(errorMsg)
        }
        return result
    }

    private fun getFloatArrayFromLog(log: JSONObject, key: String): FloatArray {
        val array = log.getJSONArray(key)
        val result = FloatArray(array.length())
        for (i in 0 until array.length()) {
            result[i] = array.getDouble(i).toFloat()
        }
        return result
    }

    private fun isShowingCard(): Boolean {
        return cardWrapper?.visibility == View.VISIBLE
    }

    internal class WebSocketHandler(view: DashboardView) : Handler() {
        private val mView: WeakReference<DashboardView> = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val view = mView.get()
            view?.serverOnMessage(msg)
        }
    }
}
