package com.example.bribriucr.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.R

class WordCardAdapter(private val cardData: List<FragmentLearnModel>) :
    RecyclerView.Adapter<WordCardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.cardImage)
        val cardText: TextView = itemView.findViewById(R.id.cardText)
        val soundIcon: ImageView = itemView.findViewById(R.id.soundIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_word, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardData[position]
        holder.cardImage.setImageResource(cardItem.imagen)
        holder.cardText.text = cardItem.nombre

        // Handle sound icon clicks (optional)
        holder.soundIcon.setOnClickListener {
            // Play audio using the audioResourceId from the model data
        }
    }

    override fun getItemCount(): Int = cardData.size
}

