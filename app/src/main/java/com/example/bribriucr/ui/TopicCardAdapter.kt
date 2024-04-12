package com.example.bribriucr.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.ImageMatch
import com.example.bribriucr.R

class TopicCardAdapter(private val topicCard: List<TopicCard>, private val context: Context,):
    RecyclerView.Adapter<TopicCardAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val context = parent.context
            ?: // Handle null context
            throw RuntimeException("Context is null in TopicCardAdapter")
        val vista = LayoutInflater.from(context).inflate(R.layout.card_topic, parent, false)
        return ItemViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemName.text = topicCard[position].name
        val imageIdString = topicCard[position].image  // Image is a string with numbers
        val imageId: Int? = imageIdString?.toIntOrNull()  // Convert string to int
        if (imageId != null) {
            val image = context.resources.getDrawable(imageId)
            holder.itemPhoto.setImageDrawable(image)
        } else {
            // Handle case where image ID conversion fails (optional: set default image)
        }
        holder.itemPercentage.text = topicCard[position].percentage
        holder.itemView.setOnClickListener {
            val selectedTopic = topicCard[position]
            navigateToTopicDetails(selectedTopic)
        }
    }

    private fun navigateToTopicDetails(topic: TopicCard) {
        val intent = Intent(context, ImageMatch::class.java)
        intent.putExtra("topic", topic.name) // Pass the selected topic data
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle activity not found exception (e.g., log error, display message)
            Log.e("TopicCardAdapter", "Activity not found for ImageMatch", e)
        }
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