package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryAdapter(
    private val context: Context,
    private var source: MutableList<Word>,
    private val listener:  (Word) -> Unit,
    private val dbHelper: DBHelper
) : BaseAdapter() {

    override fun getCount(): Int = source.size

    override fun getItem(position: Int): Word = source[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.history_layout_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val word = getItem(position)
        viewHolder.termTextView.text = word.term
        viewHolder.dateTextView.text = formatDate(word.note.toLong())

        view.setOnClickListener {
            listener(word)
        }
        // Handle delete button click
        viewHolder.btnDelete.setOnClickListener {
            // Remove the bookmark from the database
            dbHelper.removeFromTable(word, BOOKMARK_TABLE)

            // Remove the item from the list and notify the adapter
            source.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }

    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val currentDate = Calendar.getInstance()

        // Check if the date is today
        return if (calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)) {
            // Format as time (HH:mm:ss)
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)
        } else {
            // Format as date (MMM dd, yyyy)
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
        }
    }

    private class ViewHolder(view: View) {
        val termTextView: TextView = view.findViewById(R.id.history_word_term)
        val dateTextView: TextView = view.findViewById(R.id.history_word_date)
        val btnDelete:ImageView = view.findViewById(R.id.btn_delete_history)
    }

    interface OnItemClickListener {
        fun onItemClick(word: Word)
    }
}