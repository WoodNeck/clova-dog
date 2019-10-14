package com.cidteamq.clovaproject.utils

import android.content.Context;
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.cidteamq.clovaproject.R

class RecyclerAdapter(internal var context: Context, internal var items: List<Card>, internal var item_layout: Int) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val drawable = ContextCompat.getDrawable(context, item.image)
        holder.image.setBackground(drawable)
        holder.title.setText(item.title)
        holder.cardview.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
            }
        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var image: ImageView
        internal var title: TextView
        internal var cardview: CardView

        init {
            image = itemView.findViewById<ImageView>(R.id.image)
            title = itemView.findViewById<TextView>(R.id.title)
            cardview = itemView.findViewById<CardView>(R.id.cardview)
        }
    }
}
