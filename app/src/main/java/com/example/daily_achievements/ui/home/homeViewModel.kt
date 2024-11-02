package com.example.daily_achievements.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class homeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Zadej svoji aktivitu pro dnešní den."
    }
    val text: LiveData<String> = _text
}