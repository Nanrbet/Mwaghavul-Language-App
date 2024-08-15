package com.example.mwaghavullexicon

import Word
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    private val limit = 100
    init {
        _words.value = mutableListOf()
    }
    fun loadInitialWords() {
        offset = 0
        val initialWords = dbHelper.getWords(limit, offset)
        // Check if _words.value is not null and add the new words to the existing list
        val updatedWords = _words.value?.toMutableList() ?: mutableListOf()
        updatedWords.addAll(initialWords)
        // Update the LiveData with the new list
        _words.value = updatedWords
        _words.notifyObserver() // Notify observers of the new data
    }

    fun loadMoreWords() {
        offset += limit
        val moreWords = dbHelper.getWords(limit, offset)
        // Check if _words.value is not null and add the new words to the existing list
        val updatedWords = _words.value?.toMutableList() ?: mutableListOf()
        updatedWords.addAll(moreWords)
        // Update the LiveData with the new list
        _words.value = updatedWords
        _words.notifyObserver() // Notify observers of the new data
    }
    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
