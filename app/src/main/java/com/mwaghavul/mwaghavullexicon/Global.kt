package com.mwaghavul.mwaghavullexicon

import Word
import android.content.Context
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val SELECTED_DICTIONARY_KEY = "dictionary_type"

// Stable String constants for Dictionary Types - Defined here globally
const val DIC_MWA_ENG = "mwaghavul_english"
const val DIC_ENG_MWA = "english_mwaghavul"
const val DIC_ENG_ENG = "english_english"

object Global {
    private const val PREF_NAME = "mwaghavul_lexicon_prefs"
    private const val LAST_FETCH_KEY = "lastFetchDate"
    private const val CURRENT_WORD_KEY = "currentWord"

    fun saveState(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getState(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    fun saveCurrentWord(context: Context, word: Word) {
        val jsonString = Gson().toJson(word)
        saveState(context, CURRENT_WORD_KEY, jsonString)
    }

    fun getCurrentWord(context: Context): Word? {
        val jsonString = getState(context, CURRENT_WORD_KEY)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, Word::class.java)
        } else {
            null
        }
    }

    fun isNewDay(context: Context): Boolean {
        val lastFetchDate = getState(context, LAST_FETCH_KEY)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        return lastFetchDate != currentDate
    }

    fun updateLastFetchDate(context: Context) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        saveState(context, LAST_FETCH_KEY, currentDate)
    }
}
