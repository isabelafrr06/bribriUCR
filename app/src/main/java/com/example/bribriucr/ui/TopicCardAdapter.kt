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

class TopicCardAdapter(private val topicCard: List<TopicCard>, private val context: Context):
    RecyclerView.Adapter<TopicCardAdapter.ItemViewHolder>() {

    /**
     * Creates a new ViewHolder instance.
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new ItemViewHolder that holds the view of the card item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val context = context
        val vista = LayoutInflater.from(context).inflate(R.layout.card_topic, parent, false)
        return ItemViewHolder(vista)
    }

    /**
     * Binds the data of a TopicCard to the corresponding view holder.
     * @param holder The ViewHolder that holds the view of the card item.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemName.text = topicCard[position].name
        val imageIdString = topicCard[position].image  // Image is a string with numbers
        val imageId: Int? = imageIdString?.toIntOrNull()  // Convert string to int
        if (imageId != null) {
            val image = context.resources.getDrawable(imageId)
            holder.itemPhoto.setImageDrawable(image)
        } else {
            // Handle case where image ID conversion fails (optional: set default image)
            // holder.itemPhoto.setImageResource(R.drawable.default_image)
        }
        holder.itemPercentage.text = topicCard[position].percentage
        holder.itemView.setOnClickListener {
            val selectedTopic = topicCard[position]
            navigateToTopicDetails(selectedTopic)
        }
    }

    /**
     * Navigates to the ImageMatch activity with the selected topic data.
     * @param topic The selected TopicCard object.
     */
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

    /**
     * Returns the total number of items in the adapter.
     * @return The number of items in the topicCard list.
     */
    override fun getItemCount(): Int {
        return topicCard.size
    }

    /**
     * Represents a single card item view holder.
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.item_name)
        var itemPercentage: TextView = itemView.findViewById(R.id.percentage)
        var itemPhoto: ImageView = itemView.findViewById(R.id.item_photo)
    }
}
