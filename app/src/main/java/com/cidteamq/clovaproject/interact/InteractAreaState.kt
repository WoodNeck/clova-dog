package com.cidteamq.clovaproject.interact

interface InteractAreaState{
    enum class State {
        IDLE, VOICE, RESULT, FEEDBACK
    }

    fun changeState(nextState: InteractAreaState)
}
