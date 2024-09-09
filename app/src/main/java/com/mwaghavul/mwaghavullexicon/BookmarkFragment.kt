package com.mwaghavul.mwaghavullexicon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class BookmarkFragment (): Fragment()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val bookmarkList : ListView = view.findViewById(R.id.bookmark_list)
        // Implement the listener inline
        val bookmarkAdapter = BookmarkAdapter(requireActivity(), dbHelper.getAllWordsFromTable(BOOKMARK_TABLE).toMutableList(),
            listener = { word ->
                // Navigate to DetailFragment when an item is clicked
                val fragment = DetailFragment()
                word.let {
                    val bundle = Bundle().apply {
                        putParcelable("selected_word", it) // Ensure Word implements Parcelable
                    }
                    val nextFragment = fragment::class.java.newInstance().apply {
                        arguments = bundle
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            },
            dbHelper = dbHelper

        )

        bookmarkList.adapter = bookmarkAdapter
    }
}