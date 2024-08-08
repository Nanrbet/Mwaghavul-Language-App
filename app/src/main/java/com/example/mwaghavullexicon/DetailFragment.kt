package com.example.mwaghavullexicon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class DetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the word from the arguments and display it
        val selectedWord = arguments?.getString("selected_word")

        // Find the TextView and set the text
        val textView: TextView = view.findViewById(R.id.term_text)
        textView.text = selectedWord
        val posLabelTextView: TextView = view.findViewById(R.id.pos_label)
        val posTextView: TextView = view.findViewById(R.id.pos_text)

        val pronunciationLabelTextView: TextView = view.findViewById(R.id.pronunciation_label)
        val pronunciationTextView: TextView = view.findViewById(R.id.pronunciation_text)
        val speakerImageButton: ImageButton = view.findViewById(R.id.speaker_image_btn)
        val bookmarkImageButton: ImageButton = view.findViewById(R.id.bookmark_image_btn)

        val definitionLabelTextView: TextView = view.findViewById(R.id.definition_label)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text)

        val examplesLabelTextView: TextView = view.findViewById(R.id.examples_label)
        val examplesTextView: TextView = view.findViewById(R.id.examples_text)

        val translationLabelTextView: TextView = view.findViewById(R.id.translation_label)
        val translationTextView: TextView = view.findViewById(R.id.translation_text)

        bookmarkImageButton.setOnClickListener {
            val currentState = bookmarkImageButton.tag as? Int ?: 0
            bookmarkImageButton.tag = if (currentState == 0) {
                bookmarkImageButton.setImageResource(R.drawable.filled_bookmark_24)
                1
            } else {
                bookmarkImageButton.setImageResource(R.drawable.outline_bookmark_border_24)
                0
            }
        }
    }
}