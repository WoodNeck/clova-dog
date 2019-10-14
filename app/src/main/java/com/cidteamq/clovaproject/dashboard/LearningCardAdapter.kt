package com.cidteamq.clovaproject.dashboard

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cidteamq.clovaproject.DogAction
import com.cidteamq.clovaproject.R

class LearningCardAdapter(internal var context: Context) : RecyclerView.Adapter<LearningCardAdapter.ViewHolder>() {
    var items = arrayListOf<LearningInfo>()
    var ITEM_MAX_SIZE = 10
    private var dashboardView: DashboardView? = null

    override fun getItemCount(): Int = this.items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_learning_card, null)
        dashboardView = (context as Activity).findViewById(R.id.dashboard)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("POSITION ", position.toString())
        val item = items[position]
        holder.image.setImageResource(item.image)
        holder.image.setBackgroundColor(Color.parseColor(DogAction.toThemeColor(item.action)))
        holder.date.text = item.date.substring(0..18) + "-" + item.model
        holder.command.text = item.command
        holder.cardview.setOnClickListener { dashboardView?.showContent(item) }
        holder.possibility.text = "${(item.result[item.action] * 100).toInt()}%"
        holder.action.text = DogAction.toKoreanString(item.action)
        if (item.feedback == -1) {
            Log.d("-1", "Position: " + position)
            holder.feedback.setImageResource(R.drawable.ic_thumb_down)
            holder.feedback.setBackgroundDrawable(context.resources.getDrawable(R.drawable.shape_rect_button))
            holder.feedback.background.setColorFilter(context.resources.getColor(R.color.cpb_red), PorterDuff.Mode.SRC_ATOP)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var image: ImageView = itemView.findViewById(R.id.image)
        internal var date: TextView = itemView.findViewById(R.id.date)
        internal var command: TextView = itemView.findViewById(R.id.command)
        internal var cardview: CardView = itemView.findViewById(R.id.cardview)
        internal var feedback: ImageView = itemView.findViewById(R.id.feedbackIndicator)
        internal var possibility: TextView = itemView.findViewById(R.id.possibility)
        internal var action: TextView = itemView.findViewById(R.id.action)
    }
}
