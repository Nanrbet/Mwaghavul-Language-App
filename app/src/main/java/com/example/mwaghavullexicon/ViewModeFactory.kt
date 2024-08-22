package com.example.mwaghavullexicon

import Word
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelFactory(private val dbHelper: DBHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordLoader::class.java)) {
            return WordLoader(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class WordLoader(private val dbHelper: DBHelper) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> get() = _words

    private var offset = 0
    private val limit = 7
    private var initialWordsLoaded = false // Flag to track initial load
    init {
        _words.value = mutableListOf()
    }
    fun loadInitialWords() {
        offset = 0
        val initialWords = dbHelper.getWords(limit, offset)
        // Create a new list with only the initial words
        val updatedWords = initialWords.toMutableList()
        // Update the LiveData with the new list
        _words.value = updatedWords
        // Set the flag to true after loading initial words
        initialWordsLoaded = true
    }

    fun loadMoreWords() {
        // Check if initial words have been loaded
        if (!initialWordsLoaded) {
            Log.d("WordLoader", "Initial words not loaded. Skipping loadMoreWords.")
            return
        }
        offset += limit
        val moreWords = dbHelper.getWords(limit, offset)
        // Check if _words.value is not null and add the new words to the existing list
        val updatedWords = moreWords.toMutableList()
        // Update the LiveData with the new list
        _words.value = updatedWords
        _words.notifyObserver() // Notify observers of the new data
    }
    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}

class MainViewModel: ViewModel(){

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000L)
            _isReady.value = true
        }
    }
}
