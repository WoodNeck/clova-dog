package com.cidteamq.clovaproject.interact.fragment

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.interact.InteractAreaLayout
import com.cidteamq.clovaproject.interact.InteractAreaState
import com.cidteamq.clovaproject.interact.InteractView
import com.cidteamq.clovaproject.utils.DogWebSocketListener
import com.cidteamq.clovaproject.utils.WebSocketMessageBuilder
import com.dd.CircularProgressButton
import org.json.JSONObject

class InteractFeedbackView : InteractView {
    private var resultObj: JSONObject? = null
    private var resultAction : ImageView? = null
    private var resultActionTv: TextView? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.tag = InteractAreaState.State.FEEDBACK

        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_interact_feedback, this, true)

        resultAction = v.findViewById(R.id.resultAction)
        resultActionTv = v.findViewById(R.id.resultText)

        val btnOkay = v.findViewById<LinearLayout>(R.id.btnOkay)
        val btnCancel = v.findViewById<LinearLayout>(R.id.btnCancel)
        val dogActivity = context as DogActivity
        val websocket = dogActivity.listener

        btnOkay!!.setOnClickListener({
            val msgBuilder = WebSocketMessageBuilder()
            val recognition = resultObj!!.getJSONArray("recognition")
            val msg = msgBuilder.setMessageType(DogWebSocketListener.MessageType.LEARN)
                .addMessage("command", resultObj!!.getString("command"))
                .addMessage("action", resultObj!!.getInt("action"))
                .addMessage("feedback", 1)
                .addMessage("recognition", recognition)
                .build()
            websocket.send(msg)

            val parent = parent as InteractAreaLayout
            val idleView = parent.getView(InteractAreaState.State.IDLE) as InteractIdleView
            parent.swapView(this, idleView)
        })

        btnCancel!!.setOnClickListener({
            val msgBuilder = WebSocketMessageBuilder()
            val recognition = resultObj!!.getJSONArray("recognition")
            val msg = msgBuilder.setMessageType(DogWebSocketListener.MessageType.LEARN)
                .addMessage("command", resultObj!!.getString("command"))
                .addMessage("action", resultObj!!.getInt("action"))
                .addMessage("feedback", -1)
                .addMessage("recognition", recognition)
                .build()
            websocket.send(msg)

            val parent = parent as InteractAreaLayout
            val idleView = parent.getView(InteractAreaState.State.IDLE) as InteractIdleView
            parent.swapView(this, idleView)

        })
    }

    override fun hide() {
        this.visibility = View.GONE
    }

    override fun show() {
        (context as Activity).runOnUiThread({
            this.visibility = View.VISIBLE
            val action = resultObj!!.getInt("action")
            (context as DogActivity).changePose(DogAction.toActionString(action))
        })
    }

    override fun onStart() {}

    override fun onStop() {}

    fun setResult(result: JSONObject) {
        resultObj = result
        val action = result.getInt("action")
        setActionIcon(action)
        setActionText(action)
    }

    fun setActionIcon(index: Int) {
        resultAction!!.setImageResource(DogAction.toDrawable(index))
    }

    fun setActionText(index: Int) {
        resultActionTv!!.text = DogAction.toKoreanString(index)
    }
}
