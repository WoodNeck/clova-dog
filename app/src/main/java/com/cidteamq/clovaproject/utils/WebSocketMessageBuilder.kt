package com.cidteamq.clovaproject.utils

import org.json.JSONArray
import org.json.JSONObject

class WebSocketMessageBuilder{
    private val msgObj = JSONObject()

    fun setMessageType(type: DogWebSocketListener.MessageType): WebSocketMessageBuilder {
        msgObj.put("type", type.toString())
        return this
    }

    fun addMessage(name: String, value: String): WebSocketMessageBuilder {
        msgObj.put(name, value)
        return this
    }

    fun addMessage(name: String, value: Int): WebSocketMessageBuilder {
        msgObj.put(name, value)
        return this
    }

    fun addMessage(name: String, value: JSONArray): WebSocketMessageBuilder {
        msgObj.put(name, value)
        return this
    }

    fun addMessage(name: String, value: ArrayList<String>): WebSocketMessageBuilder {
        val array = JSONArray()
        for (str in value) {
            array.put(str)
        }
        msgObj.put(name, array)
        return this
    }

    fun build(): JSONObject {
        return msgObj
    }
}
