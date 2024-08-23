package com.example.mwaghavullexicon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutTextView: TextView = view.findViewById(R.id.about_text)
        aboutTextView.text = aboutText
    }
        private val aboutText: String
            get() = """
            Mwaghavul Lexicon
            -----------------
            The Mwaghavul Lexicon app is designed to help users explore the rich vocabulary of the Mwaghavul language. This app provides definitions, audio pronunciations, example sentences, and more to enhance your learning experience.

            Features:
            - Comprehensive word database
            - Audio pronunciations for accurate learning
            - Bookmark your favorite words
            - User-friendly interface

            Version: 1.0
            Developed by: Your Name
            Contact: support@mwaghavullexicon.com
        """.trimIndent()
}