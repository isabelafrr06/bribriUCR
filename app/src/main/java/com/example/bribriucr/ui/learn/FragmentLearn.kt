package com.example.bribriucr.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.R
import com.example.bribriucr.db.DbHelper

class FragmentLearn (dbHelper: DbHelper): Fragment() {

    private lateinit var titleText: TextView
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var cardAdapter: WordCardAdapter

    private var fragmentTitle: String = "Vocabulario"  // Fragment-specific title
    private var allLearned: Boolean = false  // Optional: Overall learned state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_learn, container, false)

        titleText = view.findViewById(R.id.titleText)
        cardRecyclerView = view.findViewById(R.id.cardRecyclerView)
        cardRecyclerView.layoutManager = GridLayoutManager(context, 2,
            GridLayoutManager.VERTICAL, false) // Set 2 images by row

        titleText.text = fragmentTitle

        // Prepare card data
        val cardData = listOf(
            FragmentLearnModel("Item 1", "Description 1", R.drawable.agua, R.raw.di_agua, false),
            FragmentLearnModel("Item 2", "Description 2", R.drawable.apa, R.raw.apa_lora, true),
            FragmentLearnModel("Item 3", "Description 3", R.drawable.agua, R.raw.di_agua, false),
            FragmentLearnModel("Item 4", "Description 4", R.drawable.apa, R.raw.apa_lora, true),
            //
            // Add more card data objects
        )

        cardAdapter = WordCardAdapter(cardData)
        cardRecyclerView.adapter = cardAdapter

        return view
    }

    // Handle fragment-level logic, potentially updating "allLearned" based on card data
}
