package com.example.bribriucr.ui.learn

data class FragmentLearnModel(
    val title: String,
    val description: String,
    val imageResourceId: Int,
    val audioResourceId: Int, // Optional: Resource ID for sound file
    val isLearned: Boolean  // Add a field for learned status
)