package com.cidteamq.clovaproject.interact

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class InteractAreaLayout : RelativeLayout {
    private val MAX_CHILD_VIEWS = InteractAreaState.State.values().size
    private var childViews: ArrayList<InteractView> = arrayListOf()
    private var currentState = InteractAreaState.State.IDLE

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (child is InteractView) {
            validateChildViewCount()
            childViews.add(child)
            if (childViews.size == MAX_CHILD_VIEWS) {
                initializeChildViews()
            }
        }
    }

    fun onStart() {
        for (child in childViews) {
            child.onStart()
        }
    }

    fun onStop() {
        for (child in childViews) {
            child.onStop()
        }
    }

    fun swapView(from: InteractView?, to: InteractView?){
        if (from!!.tag != to!!.tag) {
            from.hide()
            to.show()
            currentState = to.tag as InteractAreaState.State
        }
    }

    fun getView(index: InteractAreaState.State): InteractView = childViews[index.ordinal]

    fun getCurrentView(): InteractView = childViews[currentState.ordinal]

    private fun initializeChildViews() {
        childViews[0].visibility = View.VISIBLE
        for (i in 1 until MAX_CHILD_VIEWS) {
            childViews[i].visibility = View.GONE
        }
        Log.d(InteractAreaLayout::class.java.simpleName, "Childs Initialized")
    }

    private fun validateChildViewCount() {
        if (childViews.size >= MAX_CHILD_VIEWS) {
            throw IllegalArgumentException("레이아웃에 " + MAX_CHILD_VIEWS + "개를 초과하여 뷰를 추가할 수 없습니다.");
        }
    }
}
