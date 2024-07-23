package com.example.mwaghavullexicon

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast


class SearchFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable options menu in this fragment
        setHasOptionsMenu(true)
    }

    // Inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options, menu)
        //tODO: make menuSetting =menu.findItem(R.id.action_setting)
        // so that translation can change icon when selected
        val id = Global.getState(requireContext() as MainActivity, "dict_type")
        if (id != null) {
            val menuItem = menu.findItem(resources.getIdentifier(id, "id", requireContext().packageName))
            if (menuItem != null) {
                onOptionsItemSelected(menuItem)
            } else {
                Log.e("SearchFragment", "Menu item not found: $id")
                // Handle the error appropriately, e.g., show a message to the user
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mwaghavul_english -> {
                Toast.makeText(requireContext(), "Mwaghavul - English clicked", Toast.LENGTH_LONG).show()
                Global.saveState(requireActivity() as MainActivity, "dict_type", "mwaghavul_english")
                true
            }
            R.id.english_mwaghavul -> {
                Toast.makeText(requireContext(), "English - Mwaghavul clicked", Toast.LENGTH_LONG).show()
                Global.saveState(requireActivity() as MainActivity, "dict_type", "english_mwaghavul")
                true
            }
            R.id.english_english -> {
                Toast.makeText(requireContext(), "English - English clicked", Toast.LENGTH_LONG).show()
                Global.saveState(requireActivity() as MainActivity, "dict_type", "english_english")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, getListOfWords())
        listView.adapter = adapter

        // Set the item click listener
        listView.setOnItemClickListener { parent, view, position, id ->
            // Create an instance of DetailFragment
            val fragment = DetailFragment()

            // Optionally, pass data to the DetailFragment using arguments
            val bundle = Bundle()
            bundle.putString("selected_word", adapter.getItem(position))
            fragment.arguments = bundle

            // Perform the fragment transaction
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    fun getListOfWords() : Array<String>{
        val source = arrayOf(
            "a","apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew",
            "kiwi", "lemon", "mango", "nectarine", "orange", "papaya", "quince", "raspberry",
            "strawberry", "tangerine", "ugli", "vanilla", "watermelon", "xigua", "yam", "zucchini",
            "apricot", "blueberry", "cantaloupe", "dragonfruit", "eggplant"
        )
        return source
    }
}
