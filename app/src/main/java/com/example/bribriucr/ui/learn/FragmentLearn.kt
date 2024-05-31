package com.example.bribriucr.ui.learn

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bribriucr.R
import com.example.bribriucr.db.DbHelper

class FragmentLearn(helper: DbHelper) : Fragment() {

    // Reference to the helper class for database interaction
    private val dbHelper = helper

    // UI elements
    private lateinit var titleText: TextView
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var cardAdapter: WordCardAdapter

    // Media player for sound playback
    private lateinit var mediaPlayer: MediaPlayer

    /**
     * Inflates the fragment layout and sets up UI elements and listeners.
     * @param inflater LayoutInflater for inflating the layout
     * @param container ViewGroup containing the fragment
     * @param savedInstanceState Bundle containing saved state information (if any)
     * @return the inflated View for the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_learn, container, false)

        // Find UI elements by their IDs
        titleText = view.findViewById(R.id.titleText)
        cardRecyclerView = view.findViewById(R.id.cardRecyclerView)
        cardRecyclerView.layoutManager = GridLayoutManager(context, 2,
            GridLayoutManager.VERTICAL, false) // Set 2 images by row

        // Fragment title
        var fragmentTitle: String = getString(R.string.vocabulario)
        titleText.text = fragmentTitle

        // Prepare card data by fetching words from database
        val cardData = fetchWordsFromDb()
        cardAdapter = WordCardAdapter(cardData)
        cardRecyclerView.adapter = cardAdapter

        // Initialize media player and set completion listener
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()  // Release resources when done
        }

        // Add touch listener to RecyclerView for card interactions
        cardRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val view = rv.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val position = rv.getChildAdapterPosition(view)
                    // Handle card touch at position 'position'
                    playCardSound(position, cardData)  // Pass cardData as argument
                    return true  // Consume the touch event
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        return view
    }

    /**
     * Fetches words from the database using the DbHelper instance.
     * @return a list of Palabra objects representing the fetched words
     */
    private fun fetchWordsFromDb(): List<FragmentLearnModel> {
        val db = dbHelper.writableDatabase  // Get writable database
        return dbHelper.getWords(db)
    }

    /**
     * Plays the sound associated with the card at the given position.
     * @param position the index of the touched card in the data list
     */
    private fun playCardSound(position: Int, cardData: List<FragmentLearnModel>) {
        if (!mediaPlayer.isPlaying) {
            val palabra = cardData[position]
            val audioResourceId = palabra.audio // Assuming sonido is a String representing resource ID
            mediaPlayer = MediaPlayer.create(context, audioResourceId)
            mediaPlayer.start()
        }
    }

    /**
     * Releases media player resources when the fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.release()
    }
}
