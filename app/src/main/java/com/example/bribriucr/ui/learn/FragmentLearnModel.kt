package com.example.bribriucr.ui.learn

data class FragmentLearnModel(
    val nombre: String,
    val imagen: Int,
    val audio: Int,
    val isLearned: Boolean  // Add a field for learned status
)