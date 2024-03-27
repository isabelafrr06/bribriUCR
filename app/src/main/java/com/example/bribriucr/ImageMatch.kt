package com.example.bribriucr

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bribriucr.db.DbHelper

class ImageMatch : AppCompatActivity() {

    private var category: String = ""
    private var imageOptions: List<ImageOption> = listOf()
    private var correctImage: ImageOption? = null
    private var dbHelper = DbHelper(this)
    private lateinit var views: ViewBinder
    private var mediaPlayer: MediaPlayer? = null

    inner class ImageOption(val imageName: String, val image: String, val audio: String)

    class ViewBinder(activity: Activity) {
        val textToGuess: TextView = activity.findViewById(R.id.text_to_guess)
        val imageOption1: ImageView = activity.findViewById(R.id.image_option_1)
        val imageOption2: ImageView = activity.findViewById(R.id.image_option_2)
        val imageOption3: ImageView = activity.findViewById(R.id.image_option_3)
        val imageOption4: ImageView = activity.findViewById(R.id.image_option_4)
        val feedbackText: TextView = activity.findViewById(R.id.feedback)
        val nextButton: Button = activity.findViewById(R.id.next_button)
        val speaker: ImageButton = activity.findViewById(R.id.sound_button)
        // ... add other UI element bindings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_match)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Enable Back button

        views = ViewBinder(this) // Bind layout elements

        // Retrieve passed topic name
        category = intent.getStringExtra("topic")!!
        setImageOptions() // Fetch image options from DB
        updateUiWithOptions()
    }

    // Function to set image options with random selection from a category
    private fun setImageOptions() {
        val numOptions = 4
        val db = dbHelper.readableDatabase
        val allImages:ArrayList<Palabra>
        if (category == ""){
            allImages = dbHelper.getAllImages(db) // For random
        }else{
            allImages = dbHelper.getImagesForCategory(db, category) // For specific category
        }
        if (allImages.size < numOptions) {
            throw IllegalArgumentException("Category '$category' has less than $numOptions images")
        }
        val shuffledImages = allImages.shuffled() // Random order
        val options = mutableListOf<ImageOption>()
        val imageMap = HashMap<String, Int>() // Map image name to position
        for (i in 0 until numOptions) {
            val image = shuffledImages[i]
            options.add(ImageOption(image.nombrePalabra, image.rutaImagen, image.rutaAudio))
            imageMap[image.nombrePalabra] = i
        }
        this.imageOptions = options.toList()
        // Randomly choose one option as the correct image
        correctImage = options[Math.random().toInt() % options.size]
    }

    private fun updateUiWithOptions() {
        views.textToGuess.text = correctImage?.imageName  // Update word to guess
        views.speaker.setOnClickListener {
            playSound(correctImage?.audio)  // Play speaker sound on button click
        }
        // Update image views with retrieved data from imageOptions
        views.imageOption1.setImageResource( // Image path is in "image" field
            resources.getIdentifier(imageOptions[0].image, "drawable", packageName)
        )
        views.imageOption2.setImageResource(
            resources.getIdentifier(imageOptions[1].image, "drawable", packageName)
        )
        views.imageOption3.setImageResource(
            resources.getIdentifier(imageOptions[2].image, "drawable", packageName)
        )
        views.imageOption4.setImageResource(
            resources.getIdentifier(imageOptions[3].image, "drawable", packageName)
        )
        views.feedbackText.visibility = View.GONE

        val imageViews = listOf(views.imageOption1, views.imageOption2, views.imageOption3, views.imageOption4)

        val onImageClickListener = object : View.OnClickListener {
            override fun onClick(v: View) {
                val selectedImagePosition = imageViews.indexOf(v)  // Get clicked image view's position
                if (selectedImagePosition < 0) return  // Handle invalid click

                val clickedImageName = imageOptions[selectedImagePosition].imageName
                val isMatch = clickedImageName == correctImage?.imageName

                // Update feedback text based on match
                views.feedbackText.text = if (isMatch) {
                    getString(R.string.correct_answer)
                } else {
                    getString(R.string.incorrect_answer)
                }
                views.feedbackText.visibility = View.VISIBLE
                // Highlight selected image
                if (isMatch) {
                    v.setBackgroundResource(R.drawable.image_border_correct)  // Green border for correct answer
                } else {
                    v.setBackgroundResource(R.drawable.image_border_incorrect)  // Yellow border for incorrect answer
                }

                // Handle next button logic after a click
                disableImageClicks()
                views.nextButton.isEnabled = true
                views.nextButton.setOnClickListener {
                    // Reset image backgrounds to default
                    for (imageView in imageViews) {
                        imageView.setBackgroundResource(R.drawable.image_border_default)
                    }
                    enableImageClicks()
                    // Prepare for the next image match (reset options, etc.)
                    setImageOptions()  // Refetch image options
                    updateUiWithOptions()  // Update UI with new options
                }
            }
        }

        for (imageView in imageViews) {
            imageView.setOnClickListener(onImageClickListener)
        }
    }

    private fun playSound(audio: String?) {
        if (audio != null) {
            mediaPlayer = MediaPlayer.create(this, audio.toInt())
        }
        mediaPlayer?.start()
    }

    private fun disableImageClicks() {
        views.imageOption1.isClickable = false
        views.imageOption2.isClickable = false
        views.imageOption3.isClickable = false
        views.imageOption4.isClickable = false
    }

    private fun enableImageClicks() {
        views.imageOption1.isClickable = true
        views.imageOption2.isClickable = true
        views.imageOption3.isClickable = true
        views.imageOption4.isClickable = true
    }
    override fun onSupportNavigateUp(): Boolean {
        // Perform any actions before finishing (e.g., save game state)
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()  // Release media player resources when activity is destroyed
    }
}