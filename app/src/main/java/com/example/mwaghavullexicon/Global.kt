package com.example.mwaghavullexicon

import android.content.Context

object Global {
    private const val PREF_NAME = "mwaghavul_lexicon_prefs"

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
}
