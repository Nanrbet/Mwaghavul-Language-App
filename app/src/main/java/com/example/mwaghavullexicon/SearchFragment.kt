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
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.ads.mediationtestsuite.viewmodels.ViewModelFactory


class SearchFragment(private var dbHelper: DBHelper) : Fragment()  {

    private lateinit var listView: ListView
    private lateinit var adapter: WordAdapter
    private lateinit var editText : EditText
    private lateinit var viewModel: WordLoader
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        listView = view.findViewById(R.id.dictionary_search_list)
        dbHelper = DBHelper(requireContext(), null)
        // Shared preference to recall the last translations
        // Initial load
        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(DBHelper())).get(WordLoader::class.java)
        // Observe the words LiveData
        viewModel.words.observe(viewLifecycleOwner) { newWords ->
            adapter.clear() // Clear previous data if needed
            adapter.addAll(newWords)
            adapter.notifyDataSetChanged()
        }

        // Load initial words, sets the itemId to eng_mwa id if not saved preference
        viewModel.loadInitialWords()
        listView.adapter = adapter
        // Inflate the layout for this fragment
        return view
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

        return when (item.itemId) {
            R.id.mwaghavul_english -> {
                viewModel.loadInitialWords()
                Toast.makeText(requireContext(), "Mwaghavul - English clicked", Toast.LENGTH_LONG).show()
                true
            }
            R.id.english_mwaghavul -> {
                viewModel.loadInitialWords()
                Toast.makeText(requireContext(), "English - Mwaghavul clicked", Toast.LENGTH_LONG).show()
                true
            }
            R.id.english_english -> {
                viewModel.loadInitialWords()
                Toast.makeText(requireContext(), "English - English clicked", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onResume() {
//        super.onResume()
//
//        val id = Global.getState(requireActivity() as MainActivity, "dic_type")?.toInt()
//        val itemId = id?.let { resources.getIdentifier(it.toString(), "id", requireActivity().packageName) } ?: R.id.english_mwaghavul
//        // Reset the data source in the SearchFragment
          // viewModel.loadInitialWords()
//        resetDataSource(dbHelper.getAllWords(itemId, limit, 0))
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        editText = view.findViewById<EditText>(R.id.edit_search)
        adapter = WordAdapter(requireContext(), mutableListOf())
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
            val selectedWord = adapter.getItem(position)
            // Pass the selectedWord to the next fragment
            navigateToNextFragment(selectedWord, fragment::class.java, R.id.main_fragment_container)

        }

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (!isLoading && totalItemCount > 0) {
                    // Load more when reaching the bottom
                    if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                        isLoading = true
                        viewModel.loadMoreWords() // Load more words
                    }
                }
            }
        })


        // To clear write up in search box
        val editText = view.findViewById<EditText>(R.id.edit_search)
        val clearButton = view.findViewById<ImageView>(R.id.clear_button)
        clearButton.setOnClickListener {
            editText.text.clear() // Clear the text in the EditText when the clear button is clicked
        }

        // To search or return no result
        val listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        val searchButton = view.findViewById<ImageView>(R.id.search_button) // Assuming the search button is the "x" button
        searchButton.setOnClickListener {//TODO: use dbHelper to search words and also update
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
    private fun navigateToNextFragment(selectedWord: Word?, fragmentClass: Class<out Fragment>, containerId: Int) {
        selectedWord?.let {
            val bundle = Bundle().apply {
                putParcelable("selected_word", it) // Ensure Word implements Parcelable
            }
            val nextFragment = fragmentClass.newInstance().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(containerId, nextFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    public fun filterValue(value: String) {
        adapter.filter.filter(value)
        val size = adapter.count
//        for (i in 0 until size) {
//            if (adapter.getItem(i)?.startsWith(value) == true) {
//                listView.setSelection(i)
//                break // If you want to select the first item that starts with the value
//            }
//        }
        //TODO: second search filtered word
//        // Filter the adapter's data
//        val filteredList = originalList.filter { word ->
//            word.term.startsWith(value, ignoreCase = true)
//        }
//
//        // Update the adapter with the filtered list
//        adapter.updateData(filteredList)

//        // Select the first item that starts with the value
//        filteredList.firstOrNull()?.let { firstItem ->
//            val position = adapter.getPosition(firstItem)
//            listView.setSelection(position)
//        }
    }

    private fun resetDataSource(data: List<Word>, append: Boolean = false) {
//        if (append) {
//            source = source + data // Append the new data to the existing list
//        } else {
//            source = data
//        }
//
//        val termList = source.map { it.term }
//
//        // Create a new adapter if it doesn't exist
//        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, termList)
//        listView.adapter = adapter
        Toast.makeText(requireContext(), "init adapter change", Toast.LENGTH_LONG).show()

        isLoading = false
    }
}
