package com.cidteamq.clovaproject.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

abstract class InfoView : RelativeLayout {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    abstract fun hide()
    abstract fun show()
}
