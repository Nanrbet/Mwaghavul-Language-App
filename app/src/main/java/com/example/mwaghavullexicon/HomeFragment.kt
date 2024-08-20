package com.example.mwaghavullexicon

import Word
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {

    private lateinit var selectedWord: Word
    // Declare TextViews
    private lateinit var textView: TextView
    private lateinit var posTextView: TextView
    private lateinit var pronunciationTextView: TextView
    private lateinit var definitionTextView: TextView
    private lateinit var examplesTextView: TextView
    private lateinit var translationTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options, menu)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Declare TextViews
        textView = view.findViewById(R.id.term_text)
        posTextView = view.findViewById(R.id.pos_text)
        pronunciationTextView = view.findViewById(R.id.pronunciation_text)
        definitionTextView = view.findViewById(R.id.definition_text)
        examplesTextView = view.findViewById(R.id.examples_text)
        translationTextView = view.findViewById(R.id.translation_text)

        // Fetch current word from shared preferences
        val savedWord = Global.getCurrentWord(requireContext())

        // Fetch new word if it's a new day or if there's no saved word
        if (Global.isNewDay(requireContext()) || savedWord == null) {
            fetchNewWord()
        } else{
            selectedWord = savedWord
        }

        // Set up refresh button
        view.findViewById<Button>(R.id.refresh_button).setOnClickListener {
            // If it's not a new day, you might want to load the existing word
            selectedWord = dbHelper.getRandomWord()
            updateUI(selectedWord)
        }

        // Find the TextView and set the text
        textView.text = selectedWord.term

        // Part of speech
        val posLabel: CardView = view.findViewById(R.id.pos_label)
        if (selectedWord.pos.isNotEmpty()) {
            posLabel.visibility = View.VISIBLE
            posTextView.text = selectedWord.pos
        } else {
            posLabel.visibility = View.GONE
        }

        // Pronunciation
        val pronunciationLabel: CardView = view.findViewById(R.id.pronunciation_label)
        if (selectedWord.pronunciation.isNotEmpty()) {
            pronunciationLabel.visibility = View.VISIBLE
            pronunciationTextView.text = getString(R.string.pronunciation_format, selectedWord.pronunciation)
        }

        // Audio button
        val speakerImageButton: ImageButton = view.findViewById(R.id.speaker_image_btn)
        // Set click listener for audio playback if needed

        // Bookmark button
        val bookmarkImageButton: ImageButton = view.findViewById(R.id.bookmark_image_btn)
        // Check if the word is bookmarked initially
        if (dbHelper.isBookmarked(selectedWord)) {
            bookmarkImageButton.setImageResource(R.drawable.filled_bookmark_24)
            bookmarkImageButton.tag = 1 // Set tag to indicate bookmarked state
        } else {
            bookmarkImageButton.setImageResource(R.drawable.outline_bookmark_border_24)
            bookmarkImageButton.tag = 0 // Set tag to indicate non-bookmarked state
        }
        // Set click listener for bookmarking
        bookmarkImageButton.setOnClickListener {
            val currentState = bookmarkImageButton.tag as? Int ?: 0
            bookmarkImageButton.tag = if (currentState == 0) {
                dbHelper.addWordToTable(selectedWord, BOOKMARK_TABLE)
                bookmarkImageButton.setImageResource(R.drawable.filled_bookmark_24)
                1
            } else {
                dbHelper.removeFromTable(selectedWord, BOOKMARK_TABLE)
                bookmarkImageButton.setImageResource(R.drawable.outline_bookmark_border_24)
                0
            }
        }

        // Definition
        val definitionLabel: CardView = view.findViewById(R.id.definition_label)
        if (selectedWord.definition.isNotEmpty()) {
            definitionLabel.visibility = View.VISIBLE
            definitionTextView.text = selectedWord.definition
        } else {
            definitionLabel.visibility = View.GONE
        }

        // Examples
        val examplesLabel: CardView = view.findViewById(R.id.examples_label)
        if (selectedWord.examples.isNotEmpty()) {
            examplesLabel.visibility = View.VISIBLE
            examplesTextView.text = selectedWord.examples
        } else {
            examplesLabel.visibility = View.GONE
        }

        // Translation
        val translationLabel: CardView = view.findViewById(R.id.translation_label)
        if (selectedWord.translation.isNotEmpty()) {
            translationLabel.visibility = View.VISIBLE
            translationTextView.text = selectedWord.translation
        } else {
            translationLabel.visibility = View.GONE
        }
    }

    private fun fetchNewWord() {
        // Fetch a new word from the database
        selectedWord = dbHelper.getRandomWord()

        // Save the current word in shared preferences
        Global.saveCurrentWord(requireContext(), selectedWord)

        // Update the UI with the selected word
        updateUI(selectedWord)

        // Update the last fetch date
        Global.updateLastFetchDate(requireContext())
    }

    private fun updateUI(word: Word) {
        textView.text = word.term
        posTextView.text = word.pos
        pronunciationTextView.text = getString(R.string.pronunciation_format, word.pronunciation)
        examplesTextView.text = word.examples
        definitionTextView.text = word.definition
        translationTextView.text = word.translation
    }
}