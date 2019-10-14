package com.cidteamq.clovaproject.utils

import android.os.Handler
import android.os.Message
import android.util.Log
import com.cidteamq.clovaproject.DogActivity
import okhttp3.*
import org.json.JSONObject
import okio.ByteString
import org.json.JSONException

class DogWebSocketListener internal constructor(internal var dogActivity: DogActivity) : WebSocketListener() {
    enum class MessageType {
        ALERT, INIT, PRED, LEARN
    }
    private val NORMAL_CLOSURE_STATUS = 1000
    private var webSocket: WebSocket? = null
    private var handlers = mutableListOf<Handler>()
    private var client: OkHttpClient? = null
    private var url = "ws://SERVER_IP_HERE/?user_name=%s&dog_name=%s"

    fun setUrl(userName: String, dogName: String) {
        url = String.format(url, userName, dogName.toLowerCase())
    }

    fun addHandler(handler: Handler) {
        handlers.add(handler)
    }

    fun start() {
        val request = Request.Builder().url(url).build()
        client = OkHttpClient()
        client!!.newWebSocket(request, this)
        client!!.dispatcher()?.executorService()?.shutdown()
    }

    fun close() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
    }

    fun send(msgObj: JSONObject) {
        webSocket!!.send(msgObj.toString())
        Log.d(TAG, webSocket.toString() + "\n" + msgObj.toString())
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        this.webSocket = webSocket
        Log.d(TAG, String.format("Websocket Opened: %s", url))
        dogActivity.onWebSocketOpen()
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d(TAG, "Message Received\n" + text)

        try {
            val obj = JSONObject(text)
            if (obj.has("type")) {
                val type = typeToInt(obj["type"] as String)
                handlers
                    .map { Message.obtain(it, type, text) }
                    .forEach { it.sendToTarget() }
            }
        } catch (e: JSONException) {
            Log.d(TAG, e.toString())
        } catch (e: NoSuchElementException) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        Log.d(TAG, "ByteString Detected: " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        Log.d(TAG, "WebSocket Closing.")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        Log.d(TAG, t?.message.toString())
        dogActivity.onWebSocketFailure()
    }

    companion object {
        private val TAG = DogWebSocketListener::class.java.simpleName
    }

    private fun typeToInt(type: String) : Int = when (type) {
        "ALERT" -> MessageType.ALERT.ordinal
        "INIT" -> MessageType.INIT.ordinal
        "PRED" -> MessageType.PRED.ordinal
        "LEARN" -> MessageType.LEARN.ordinal
        else -> -1
    }
}
