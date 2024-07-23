package com.example.mwaghavullexicon

import android.content.Context

object Global {
    fun saveState(activity: MainActivity, key: String, value: String) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getState(activity: MainActivity, key: String): String? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }
}