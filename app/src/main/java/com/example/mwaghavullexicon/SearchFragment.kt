package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class SearchFragment() : Fragment()  {

    private lateinit var listView: ListView
    private lateinit var adapter: WordAdapter
    private lateinit var editText : EditText
    private lateinit var viewModel: WordLoader
    private val adapterWordList: MutableList<Word> = mutableListOf()
    private var isLoading = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize the adapter
        listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        editText = view.findViewById<EditText>(R.id.edit_search)
        // Observe the words LiveData and update adapter when new data is loaded
        adapter = WordAdapter(requireContext(), adapterWordList) { word ->
            onWordClicked(word) // Handle the click event
        }
        listView.adapter = adapter
        // Initialize the ViewModel with the DBHelper instance
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(dbHelper)).get(WordLoader::class.java)
        viewModel.words.observe(viewLifecycleOwner) { newWords ->
            Log.d("SearchFragment", "New words received: $newWords")
            // Initialize the adapter with the loaded words

            // Only update data if new words are received and not loading
            if (newWords.isNotEmpty() && !isLoading) {
                adapterWordList.addAll(newWords)
                adapter.updateData(adapterWordList)
            }
            isLoading = false // Reset loading state after data is updated
        }


        // Set the TextWatcher for the EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.clear()
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    isLoading = true
                    viewModel.searchWords(searchText) // Search words when text changes
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // To clear write up in search box
        val editText = view.findViewById<EditText>(R.id.edit_search)
        val clearButton = view.findViewById<ImageView>(R.id.clear_button)
        clearButton.setOnClickListener {
            editText.text.clear() // Clear the text in the EditText when the clear button is clicked
        }

        // To search or return no result
        val searchButton = view.findViewById<ImageView>(R.id.search_button) // Assuming the search button is the "x" button
        searchButton.setOnClickListener {
            val searchText = editText.text.toString().trim() // Get the search text from the EditText and trim any leading or trailing spaces
            if (searchText.isNotEmpty()) {
                viewModel.searchWords(searchText)
            }
            // If no match found or the adapter is empty, show "No Results"
            Toast.makeText(requireContext(), "No Results", Toast.LENGTH_SHORT).show()
        }

        // Set up infinite scrolling
        setupInfiniteScrollListener()
    }
    private fun setupInfiniteScrollListener() {
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (!isLoading) {
                    // Calculate the last visible item indexo
                    val lastVisibleItem = firstVisibleItem + visibleItemCount
                    // Load more when reaching the bottom
                    if (lastVisibleItem >= totalItemCount - 10) {
                        isLoading = true
                        val query = editText.text.toString().trim()

                        if (query.isNotEmpty()) {
                            // Load more search results
                            viewModel.loadMoreSearchResults(query)
                        } else {
                            // Load more initial words
                            viewModel.loadMoreWords()
                        }
                    }
                }
            }
        })
    }
    private fun onWordClicked(word: Word) {
        // Create an instance of DetailFragment
        val fragment = DetailFragment()
        // Pass the selectedWord to the next fragment
        navigateToNextFragment(word, fragment::class.java, R.id.main_fragment_container)
    }
    private fun navigateToNextFragment(selectedWord: Word?, fragmentClass: Class<out Fragment>, containerId: Int) {
        if (selectedWord != null) {
            dbHelper.addWordToTable(selectedWord, HISTORY_TABLE)
        }
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

    // Inflate the menu
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options, menu)
        //tODO: make menuSetting =menu.findItem(R.id.action_setting)
        // so that translation can change icon when selected
        val id = Global.getState(requireContext(), SELECTED_DICTIONARY_KEY)?.toIntOrNull()
        if (id != null) {
            val menuItem = menu.findItem(id)
            if (menuItem != null) {
                onOptionsItemSelected(menuItem)
            }
        } else {
            onOptionsItemSelected(menu.findItem(R.id.english_mwaghavul)!!)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (item.itemId) {
            R.id.mwaghavul_english -> {
                Global.saveState(requireActivity(), SELECTED_DICTIONARY_KEY, id.toString())
                loadWordsBasedOnQuery()
                true
            }
            R.id.english_mwaghavul -> {
                Global.saveState(requireActivity(), SELECTED_DICTIONARY_KEY, id.toString())
                loadWordsBasedOnQuery()
                true
            }
            R.id.english_english -> {
                Global.saveState(requireActivity(), SELECTED_DICTIONARY_KEY, id.toString())
                loadWordsBasedOnQuery()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun loadWordsBasedOnQuery() {
        adapter.clear()
        adapterWordList.clear()
        val query = editText.text.toString().trim()

        if (query.isEmpty()) {
            // Load initial words if query is empty
            viewModel.loadInitialWords()
        } else {
            // Load search results if there is a query
            viewModel.searchWords(query)
        }
    }

    override fun onPause() {
        super.onPause()
        // Reset the isLoading flag and clear the adapter
        isLoading = false
        // Detach the scroll listener when the fragment is paused
        listView.setOnScrollListener(null)
    }
    override fun onResume() {
        super.onResume()
        // Reattach the scroll listener when the fragment is resumed
        resetListView()

    }
    private fun resetListView() {
        // Assuming you have a reference to your ListView and Adapter
        listView.adapter = null // Clear the adapter
        adapter = WordAdapter(requireContext(), adapterWordList) { word ->
            onWordClicked(word) // Handle the click event
        }
        listView.adapter = adapter // Reinitialize the adapter with your data
    }

    override fun onStop() {
        super.onStop()
        // Reset the isLoading flag to ensure fresh state when returning
        isLoading = false
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

}
