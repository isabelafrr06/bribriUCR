package com.example.bribriucr.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.R
import android.view.LayoutInflater
import android.view.ViewGroup

class TopicCardAdapter(val topicCard: List<TopicCard>, private val context: Context,):
    RecyclerView.Adapter<TopicCardAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.card_topic, parent, false)
        return ItemViewHolder(vista)}

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemName.text = topicCard[position].name
        //holder.itemPhoto.setImageResource(topicCard[position].image)
        val imageIdString = topicCard[position].image  // Assuming image is a string
        val imageId: Int? = imageIdString?.toIntOrNull()  // Handle potential conversion errors

        if (imageId != null) {
            val image = context.resources.getDrawable(imageId)
            holder.itemPhoto.setImageDrawable(image)
        } else {
            // Handle case where image ID conversion fails (optional: set default image)
        }

        holder.itemPercentage.text = topicCard[position].percentage
        //holder.itemPhoto = (topicCard[position].image.)
    }

    override fun getItemCount(): Int {
        return topicCard.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.item_name)
        var itemPercentage: TextView = itemView.findViewById(R.id.percentage)
        var itemPhoto: ImageView = itemView.findViewById(R.id.item_photo)
    }
}