package com.cidteamq.clovaproject.interact

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

abstract class InteractView : RelativeLayout{
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    abstract fun onStart()
    abstract fun onStop()
    abstract fun hide()
    abstract fun show()
}
