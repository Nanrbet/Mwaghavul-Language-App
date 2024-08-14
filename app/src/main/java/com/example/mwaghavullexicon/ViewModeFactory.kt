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
        if (modelClass.isAssignableFrom(ViewModelFactory::class.java)) {
            return ViewModelFactory(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> get() = _words

    private var currentOffset = 0
    private val limit = 20 // Adjust your limit as needed
    public isLoading = false

    fun loadMoreWords() {
        viewModelScope.launch {
            val moreWords = dbHelper.getMoreWords(limit, currentOffset)
            if (moreWords.isNotEmpty()) {
                currentOffset += moreWords.size
                _words.postValue(moreWords)
                isLoading = false // Reset loading state after loading
            } else {
                // Handle no more words scenario
                isLoading = false
            }
        }
    }

    fun loadInitialWords() {
        viewModelScope.launch {
            currentOffset = 0 // Reset offset for initial load
            val initialWords = dbHelper.getInitialWords(limit, currentOffset)
            _words.postValue(initialWords)
            currentOffset += initialWords.size // Update the offset
        }
    }


}