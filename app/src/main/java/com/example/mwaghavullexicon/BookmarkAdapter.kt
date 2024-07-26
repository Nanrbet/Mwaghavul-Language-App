package com.example.mwaghavullexicon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class BookmarkAdapter(private val context: Context, private val source: MutableList<String>, private val listener: OnItemClickListener, private val listenerBookmarkDelete: OnItemClickListener) : BaseAdapter(){
    override fun getCount(): Int {
        return source.size
    }

    override fun getItem(p0: Int): Any {
        return source[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
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
        // clicking the bookmarked words
        viewHolder.textView.text = source[position]
        viewHolder.textView.setOnClickListener {
            listener.onItemClick(source[position])
        }
        // deleting the words also
        viewHolder.btnDelete.setOnClickListener {
            listenerBookmarkDelete.onItemClick(source[position])
            // Remove item from source list and notify adapter
            source.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }

    private class ViewHolder(view: View) {
        val textView: TextView = view.findViewById(R.id.bookmark_word)
        val btnDelete: ImageView = view.findViewById(R.id.bookmark_icon)
    }
    interface OnItemClickListener {
        fun onItemClick(item: String)
    }

}