package com.example.mwaghavullexicon

import Word
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
    private val limit = 100
    private var initialWordsLoaded = false // Flag to track initial load
    private var initialSearchWordsLoaded = false // Flag to track initial load
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
        _words.notifyObserver()
    }

    fun loadMoreWords() {
        // Check if initial words have been loaded
        if (!initialWordsLoaded) {
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


    fun searchWords(query: String) {
        offset = 0 // Reset offset for a new search
        val searchResults = dbHelper.searchWords(query, limit, offset)
        _words.value = searchResults.toMutableList()
        initialSearchWordsLoaded = true
        _words.notifyObserver()
    }

    fun loadMoreSearchResults(query: String) {
        if (!initialSearchWordsLoaded) return // Ensure initial results are loaded
        offset += limit
        val moreResults = dbHelper.searchWords(query, limit, offset)
        val updatedWords = _words.value?.toMutableList() ?: mutableListOf()
        updatedWords.addAll(moreResults)
        _words.value = updatedWords // Append new results
        _words.notifyObserver() // Notify observers of the new data
    }

}
