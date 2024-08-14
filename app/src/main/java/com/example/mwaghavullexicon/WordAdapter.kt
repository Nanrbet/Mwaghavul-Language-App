package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class WordAdapter(context: Context, private var wordList: List<Word>) : ArrayAdapter<Word>(context, 0, wordList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val word = getItem(position)

        // Display only the term
        view.findViewById<TextView>(android.R.id.text1).text = word?.term

        return view
    }
    fun updateData(newList: List<Word>) {
        wordList = newList
        notifyDataSetChanged()
    }
}