package com.mwaghavul.mwaghavullexicon

import Word
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider


class SearchFragment : Fragment()  {

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
            return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMenu()
        
        listView = view.findViewById<ListView>(R.id.dictionary_search_list)
        editText = view.findViewById<EditText>(R.id.edit_search)
        
        adapter = WordAdapter(requireContext(), adapterWordList) { word ->
            onWordClicked(word)
        }
        listView.adapter = adapter
        
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(dbHelper)).get(WordLoader::class.java)
        viewModel.words.observe(viewLifecycleOwner) { newWords ->
            if (newWords.isNotEmpty() && !isLoading) {
                adapterWordList.addAll(newWords)
                adapter.updateData(adapterWordList)
            }
            isLoading = false
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.clear()
                adapterWordList.clear()
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    isLoading = true
                    viewModel.searchWords(searchText)
                } else {
                    viewModel.loadInitialWords()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val clearButton = view.findViewById<ImageView>(R.id.clear_button)
        clearButton.setOnClickListener {
            editText.text.clear()
        }

        val searchButton = view.findViewById<ImageView>(R.id.search_button)
        searchButton.setOnClickListener {
            val searchText = editText.text.toString().trim()
            if (searchText.isNotEmpty()) {
                viewModel.searchWords(searchText)
            } else {
                Toast.makeText(requireContext(), "No Results", Toast.LENGTH_SHORT).show()
            }
        }

        setupInfiniteScrollListener()
        
        // Initial load
        if (adapterWordList.isEmpty()) {
            viewModel.loadInitialWords()
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val newKey = when (menuItem.itemId) {
                    R.id.mwaghavul_english -> DIC_MWA_ENG
                    R.id.english_mwaghavul -> DIC_ENG_MWA
                    R.id.english_english -> DIC_ENG_ENG
                    else -> return false
                }
                
                Global.saveState(requireActivity(), SELECTED_DICTIONARY_KEY, newKey)
                loadWordsBasedOnQuery()
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupInfiniteScrollListener() {
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (!isLoading) {
                    val lastVisibleItem = firstVisibleItem + visibleItemCount
                    if (lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0) {
                        isLoading = true
                        val query = editText.text.toString().trim()
                        if (query.isNotEmpty()) {
                            viewModel.loadMoreSearchResults(query)
                        } else {
                            viewModel.loadMoreWords()
                        }
                    }
                }
            }
        })
    }

    private fun onWordClicked(word: Word) {
        navigateToDetailFragment(word)
    }

    private fun navigateToDetailFragment(selectedWord: Word) {
        dbHelper.addWordToTable(selectedWord, HISTORY_TABLE)
        val bundle = Bundle().apply {
            putParcelable("selected_word", selectedWord)
        }
        val detailFragment = DetailFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadWordsBasedOnQuery() {
        adapter.clear()
        adapterWordList.clear()
        val query = editText.text.toString().trim()
        if (query.isEmpty()) {
            viewModel.loadInitialWords()
        } else {
            viewModel.searchWords(query)
        }
    }

    override fun onPause() {
        super.onPause()
        isLoading = false
        listView.setOnScrollListener(null)
    }

    override fun onResume() {
        super.onResume()
        resetListView()
    }

    private fun resetListView() {
        listView.adapter = null
        adapter = WordAdapter(requireContext(), adapterWordList) { word ->
            onWordClicked(word)
        }
        listView.adapter = adapter
    }
}