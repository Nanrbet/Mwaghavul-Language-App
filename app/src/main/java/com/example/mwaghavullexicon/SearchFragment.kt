package com.example.mwaghavullexicon

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import java.io.Serializable


class SearchFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var editText : EditText

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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = parentFragmentManager.findFragmentById(R.id.main_fragment_container) as? SearchFragment
        return when (item.itemId) {
            R.id.mwaghavul_english -> {
                currentFragment?.resetDataSource(DB.getData(item.itemId))
                Toast.makeText(requireContext(), "Mwaghavul - English clicked", Toast.LENGTH_LONG).show()
                Global.saveState(requireActivity() as MainActivity, "dict_type", "mwaghavul_english")
                true
            }
            R.id.english_mwaghavul -> {
                currentFragment?.resetDataSource(DB.getData(item.itemId))
                Toast.makeText(requireContext(), "English - Mwaghavul clicked", Toast.LENGTH_LONG).show()
                Global.saveState(requireActivity() as MainActivity, "dict_type", "english_mwaghavul")
                true
            }
            R.id.english_english -> {
                currentFragment?.resetDataSource(DB.getData(item.itemId))
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

        listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        editText = view.findViewById<EditText>(R.id.edit_search)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, getListOfWords())
        listView.adapter = adapter

        // Set the TextWatcher for the EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterValue(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Set the item click listener
        listView.setOnItemClickListener { parent, view, position, id ->
            // Create an instance of DetailFragment
            val fragment = DetailFragment()
            // Pass data to the DetailFragment using arguments
            val bundle = Bundle().apply {
                putString("selected_word", adapter.getItem(position))
            }
            fragment.arguments = bundle
            // Perform the fragment transaction
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.main_fragment_container, fragment)
                addToBackStack(null)
                commit()
            }
        }
        // Sharedpreference to recall the last translations
        val id = Global.getState(requireContext() as MainActivity, "dict_type")
        if (id != null) {
            val itemId = resources.getIdentifier(id, "id", requireContext().packageName)
            resetDataSource(DB.getData(itemId))
        } else {
            resetDataSource(DB.getData(R.id.english_mwaghavul))
        }

        // To clear write up in search box
        val editText = view.findViewById<EditText>(R.id.edit_search)
        val clearButton = view.findViewById<ImageView>(R.id.clear_button)

        clearButton.setOnClickListener {
            editText.text.clear() // Clear the text in the EditText when the clear button is clicked
        }

        // To search or return no result
        val listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        val searchButton = view.findViewById<ImageView>(R.id.search_button) // Assuming the search button is the "x" button

        searchButton.setOnClickListener {
            val searchText = editText.text.toString().trim() // Get the search text from the EditText and trim any leading or trailing spaces
            val adapter = listView.adapter as ArrayAdapter<String> // Assuming you are using an ArrayAdapter<String> for the ListView

            if (adapter.count > 0) {
                for (i in 0 until adapter.count) {
                    val item = adapter.getItem(i)
                    if (item != null) {
                        if (item.startsWith(searchText, ignoreCase = true)) {
                            // If a matching item is found, select it in the ListView
                            listView.setSelection(i)
                            return@setOnClickListener
                        }
                    }
                }
            }
            // If no match found or the adapter is empty, show "No Results"
            Toast.makeText(requireContext(), "No Results", Toast.LENGTH_SHORT).show()
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
    public fun filterValue(value: String) {
        adapter.filter.filter(value)
        val size = adapter.count
        for (i in 0 until size) {
            if (adapter.getItem(i)?.startsWith(value) == true) {
                listView.setSelection(i)
                break // If you want to select the first item that starts with the value
            }
        }
    }
    fun resetDataSource(data: Array<String>) {
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        listView.adapter = adapter
    }
}
