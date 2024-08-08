package com.example.mwaghavullexicon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class BookmarkFragment : Fragment() {
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
        val bookmarkAdapter = BookmarkAdapter(requireActivity(), getListOfWords().toMutableList(), object : BookmarkAdapter.OnItemClickListener {
            override fun onItemClick(item: String) {
                Toast.makeText(requireActivity(), "Clicked: $item", Toast.LENGTH_SHORT).show()

                // Navigate to DetailFragment when an item is clicked
                val fragment = DetailFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.main_fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        },
            object : BookmarkAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    Toast.makeText(requireActivity(), "Deleted: $item", Toast.LENGTH_SHORT).show()
                    // The item removal is already handled in the adapter's btnDelete click listener
                }
            })

        bookmarkList.adapter = bookmarkAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_clear, menu)
    }

    private fun getListOfWords() : Array<String>{
        val source = arrayOf(
            "a","apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew",
            "kiwi", "lemon", "mango", "nectarine", "orange", "papaya", "quince", "raspberry",
            "strawberry", "tangerine", "ugli", "vanilla", "watermelon", "xigua", "yam", "zucchini",
            "apricot", "blueberry", "cantaloupe", "dragonfruit", "eggplant"
        )
        return source
    }

}