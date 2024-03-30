package com.example.bribriucr.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aquí aparecen los créditos de la aplicación"
    }
    val text: LiveData<String> = _text
}