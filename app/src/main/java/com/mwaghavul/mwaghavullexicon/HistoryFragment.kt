package com.mwaghavul.mwaghavullexicon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        // Manage history entries
        manageHistoryEntries()
        val historyList: ListView = view.findViewById(R.id.history_list)

        // Implement the listener inline
        val historyAdapter = HistoryAdapter(requireActivity(), dbHelper.getAllWordsFromTable(HISTORY_TABLE).toMutableList(),
            listener = { word ->
                // Navigate to DetailFragment when an item is clicked
                val bundle = Bundle().apply {
                    putParcelable("selected_word", word)
                }
                val detailFragment = DetailFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            dbHelper = dbHelper
        )

        historyList.adapter = historyAdapter
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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