package com.example.mwaghavullexicon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment(private var dbHelper: DBHelper) : Fragment() {
    private val HISTORY_TABLE = "history_table"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Manage history entries
        manageHistoryEntries()
        val historyList: ListView = view.findViewById(R.id.history_list)

        // Implement the listener inline
        val historyAdapter = HistoryAdapter(requireActivity(), dbHelper.getAllWordsFromTable(HISTORY_TABLE).toMutableList(),
            listener = { word ->
                Toast.makeText(requireActivity(), "Clicked: ${word.term}", Toast.LENGTH_SHORT).show()

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

        historyList.adapter = historyAdapter
    }

    fun manageHistoryEntries() {
        // Check the current count of history entries
        val currentCount = dbHelper.getHistoryCount()

        // If there are more than 300 entries, delete the oldest ones
        if (currentCount > 300) {
            // Delete the oldest entries
            dbHelper.deleteOldestEntries(currentCount - 300)
        }
    }

}