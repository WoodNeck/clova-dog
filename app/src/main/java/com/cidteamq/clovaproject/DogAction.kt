package com.cidteamq.clovaproject

class DogAction {
    enum class state {SIT, LAY, JUMP, BARK, BANG, EAT}

    companion object {
        fun toKoreanString(index: Int) : String = when (index) {
            state.SIT.ordinal -> "앉아"
            state.LAY.ordinal -> "엎드려"
            state.JUMP.ordinal -> "점프"
            state.BARK.ordinal -> "짖어"
            state.BANG.ordinal -> "빵"
            state.EAT.ordinal -> "먹어"
            else -> ""
        }

        fun toActionString(index: Int) : String = when (index) {
            state.SIT.ordinal -> "Sit"
            state.LAY.ordinal -> "Lay"
            state.JUMP.ordinal -> "Jump"
            state.BARK.ordinal -> "Bark"
            state.BANG.ordinal -> "Bang"
            state.EAT.ordinal -> "Eat"
            else -> ""
        }

        fun toDrawable(index: Int) : Int = when (index) {
            state.SIT.ordinal -> R.drawable.ic_sit
            state.LAY.ordinal -> R.drawable.ic_lay
            state.JUMP.ordinal -> R.drawable.ic_jump
            state.BARK.ordinal -> R.drawable.ic_bark
            state.BANG.ordinal -> R.drawable.ic_bang
            state.EAT.ordinal -> R.drawable.ic_eat
            else -> -1
        }

        fun toThemeColor(index: Int) : String = when (index) {
            state.SIT.ordinal -> "#66bb6a"
            state.LAY.ordinal -> "#ab47bc"
            state.JUMP.ordinal -> "#26c6da"
            state.BARK.ordinal -> "#ffca28"
            state.BANG.ordinal -> "#ec407a"
            state.EAT.ordinal -> "#8d6e63"
            else -> "#ffffff"
        }

        fun toDarkThemeColor(index: Int) : String = when (index) {
            state.SIT.ordinal -> "#338a3e"
            state.LAY.ordinal -> "#790e8b"
            state.JUMP.ordinal -> "#0095a8"
            state.BARK.ordinal -> "#c79a00"
            state.BANG.ordinal -> "#b4004e"
            state.EAT.ordinal -> "#5f4339"
            else -> "#000000"
        }
    }
}
