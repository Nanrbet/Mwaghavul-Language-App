package com.example.mwaghavullexicon

import Word
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

private var selectedWord: Word? = null

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dbHelper = DBHelper(requireContext(), null)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the word from the arguments and display it
        selectedWord = arguments?.getParcelable("selected_word")

        // Find the TextView and set the text
        val textView: TextView = view.findViewById(R.id.term_text)
        textView.text = selectedWord?.term ?: "No term found"
        // Part of speech
        val posLabel: CardView = view.findViewById(R.id.pos_label)
        val posTextView: TextView = view.findViewById(R.id.pos_text)
        if (!selectedWord?.pos.isNullOrEmpty()) {
            posLabel.visibility = View.VISIBLE
            posTextView.text = selectedWord?.pos
        } else {
            posLabel.visibility = View.GONE
        }

        // Pronunciation
        val pronunciationLabel: CardView = view.findViewById(R.id.pronunciation_label)
        val pronunciationTextView: TextView = view.findViewById(R.id.pronunciation_text)
        if (!selectedWord?.pronunciation.isNullOrEmpty()) {
            pronunciationLabel.visibility = View.VISIBLE
            pronunciationTextView.text = getString(R.string.pronunciation_format, selectedWord!!.pronunciation)
        }

        // Audio button
        val speakerImageButton: ImageButton = view.findViewById(R.id.speaker_image_btn)
        // Set click listener for audio playback if needed

        // Bookmark button
        val bookmarkImageButton: ImageButton = view.findViewById(R.id.bookmark_image_btn)
        // Check if the word is bookmarked initially
        if (selectedWord != null && dbHelper.isBookmarked(selectedWord!!)) {
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
                if (selectedWord != null) {
                    dbHelper.addWordToTable(selectedWord!!, BOOKMARK_TABLE)
                }
                bookmarkImageButton.setImageResource(R.drawable.filled_bookmark_24)
                1
            } else {
                if (selectedWord != null) {
                    dbHelper.removeFromTable(selectedWord!!, BOOKMARK_TABLE)
                }
                bookmarkImageButton.setImageResource(R.drawable.outline_bookmark_border_24)
                0
            }
        }

        // Definition
        val definitionLabel: CardView = view.findViewById(R.id.definition_label)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text)
        if (!selectedWord?.definition.isNullOrEmpty()) {
            definitionLabel.visibility = View.VISIBLE
            definitionTextView.text = selectedWord?.definition
        } else {
            definitionLabel.visibility = View.GONE
        }

        // Examples
        val examplesLabel: CardView = view.findViewById(R.id.examples_label)
        val examplesTextView: TextView = view.findViewById(R.id.examples_text)
        if (!selectedWord?.examples.isNullOrEmpty()) {
            examplesLabel.visibility = View.VISIBLE
            examplesTextView.text = selectedWord?.examples
        } else {
            examplesLabel.visibility = View.GONE
        }

        // Translation
        val translationLabel:CardView = view.findViewById(R.id.translation_label)
        val translationTextView: TextView = view.findViewById(R.id.translation_text)
        if (!selectedWord?.translation.isNullOrEmpty()) {
            translationLabel.visibility = View.VISIBLE
            translationTextView.text = selectedWord?.translation
        } else {
            translationLabel.visibility = View.GONE
        }
    }

    // Method to get the selected word
    fun getSelectedWord(): String {
        return selectedWord?.term ?: "No word selected"
    }
}