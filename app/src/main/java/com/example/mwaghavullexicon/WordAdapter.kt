package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class WordAdapter(context: Context, private var wordList: List<Word>, private val itemClickListener: (Word) -> Unit // Lambda for click handling
    ) : ArrayAdapter<Word>(context, 0, wordList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        val word = getItem(position)
        // Display only the term
        view.findViewById<TextView>(R.id.wordTerm_TextView).text = word?.term

        // Set click listener to pass the entire Word object
        view.setOnClickListener {
            if (word != null) {
                itemClickListener(word) // Pass the whole Word object
            }
        }
        return view
    }

    fun updateData(newList: List<Word>) {
        if (newList != wordList){
            wordList = newList
            Log.d("SearchFragment", "updateAdapter word")}
        notifyDataSetChanged()
    }
}