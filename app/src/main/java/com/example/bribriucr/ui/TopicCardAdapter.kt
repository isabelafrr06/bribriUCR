package com.example.bribriucr.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.R
import android.view.LayoutInflater
import android.view.ViewGroup

class TopicCardAdapter(val topicCard: List<TopicCard>):
    RecyclerView.Adapter<TopicCardAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicCardAdapter.ItemViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.card_topic, parent, false)
        return ItemViewHolder(vista)}

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemName.text = topicCard[position].name
        holder.itemPhoto.text = topicCard[position].image
        holder.itemPercentage.text = topicCard[position].percentage
        //holder.itemPhoto = (topicCard[position].image.)
    }

    override fun getItemCount(): Int {
        return topicCard.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.item_name)
        var itemPercentage: TextView = itemView.findViewById(R.id.percentage)
        var itemPhoto: TextView = itemView.findViewById(R.id.item_photo)
    }
}