package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class BookmarkAdapter(
    private val context: Context,
    private var wordList: MutableList<Word>,  // MutableList to allow modifications
    private val listener: (Word) -> Unit,
    private val dbHelper: DBHelper // Pass the DBHelper to interact with the database
) : BaseAdapter() {

    override fun getCount(): Int {
        return wordList.size
    }

    override fun getItem(position: Int): Any {
        return wordList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bookmark_layout_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val word = wordList[position]

        // Set the term of the word
        viewHolder.textView.text = word.term

        // Handle word click
        viewHolder.textView.setOnClickListener {
            listener(word)
        }

        // Handle delete button click
        viewHolder.btnDelete.setOnClickListener {
            // Remove the bookmark from the database
            dbHelper.removeBookmark(word)

            // Remove the item from the list and notify the adapter
            wordList.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }

    // Function to update the data in the adapter
    fun updateData(newList: List<Word>) {
        wordList = newList.toMutableList()
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        val textView: TextView = view.findViewById(R.id.bookmark_word)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete_bookmark)
    }
    interface OnItemClickListener {
        fun onItemClick(word: Word)
    }

}