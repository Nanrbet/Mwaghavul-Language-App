package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.Fragment


class SearchFragment(private val dbHelper: DBHelper) : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var editText : EditText

    private var source : List<Word> = emptyList()

    interface DataSourceCallback {
        fun onDataSourceReady(source: List<Word>)
    }

    private var dataSourceCallback: DataSourceCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
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
        val id = Global.getState(requireContext() as MainActivity, "dic_type")?.toIntOrNull()
        if (id != null) {
            val menuItem = menu.findItem(id)
            if (menuItem != null) {
                onOptionsItemSelected(menuItem)
            }
        } else {
            onOptionsItemSelected(menu.findItem(R.id.english_mwaghavul)!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Global.saveState(requireActivity() as MainActivity, "dic_type", id.toString())
        source = dbHelper.getAllWords(id)
        val currentFragment = parentFragmentManager.findFragmentById(R.id.main_fragment_container) as? SearchFragment

        return when (item.itemId) {
            R.id.mwaghavul_english -> {
                dataSourceCallback?.onDataSourceReady(source)
                currentFragment?.resetDataSource(source)
                Toast.makeText(requireContext(), "Mwaghavul - English clicked", Toast.LENGTH_LONG).show()
                true
            }
            R.id.english_mwaghavul -> {
                dataSourceCallback?.onDataSourceReady(source)
                currentFragment?.resetDataSource(source)
                Toast.makeText(requireContext(), "English - Mwaghavul clicked", Toast.LENGTH_LONG).show()
                true
            }
            R.id.english_english -> {
                dataSourceCallback?.onDataSourceReady(source)
                currentFragment?.resetDataSource(source)
                Toast.makeText(requireContext(), "English - English clicked", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is DataSourceCallback) {
//            dataSourceCallback = context
//        } else {
//            throw RuntimeException("$context must implement DataSourceCallback")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        dataSourceCallback = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        editText = view.findViewById<EditText>(R.id.edit_search)
        // Create a list of word.term values
        val termList = source.map { it.term }
        // Create an ArrayAdapter with the termList
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, termList)
//        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, source)
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
        // Call the callback to get the data source
        dataSourceCallback?.onDataSourceReady(source)
        // Shared preference to recall the last translations
        val id = Global.getState(requireContext() as MainActivity, "dict_type")
        if (id != null) {
            val itemId = resources.getIdentifier(id, "id", requireContext().packageName)
            dataSourceCallback?.onDataSourceReady(dbHelper.getAllWords(itemId))
            resetDataSource(dbHelper.getAllWords(itemId))
        } else {
            dataSourceCallback?.onDataSourceReady(dbHelper.getAllWords(R.id.english_mwaghavul))
            resetDataSource(dbHelper.getAllWords(R.id.english_mwaghavul))
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

    fun setDataSourceCallback(callback: DataSourceCallback) {
        this.dataSourceCallback = callback
    }

    public fun resetDataSource(data: List<Word>) {
        source = data
        val termList = source.map { it.term }

        if (adapter == null || adapter.isEmpty) {
            // Create a new adapter if it doesn't exist or is empty
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, termList)
            listView.adapter = adapter
        } else {
            // Clear the existing adapter and update the term list
            adapter.clear()
            adapter.addAll(termList)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {

    }
}
