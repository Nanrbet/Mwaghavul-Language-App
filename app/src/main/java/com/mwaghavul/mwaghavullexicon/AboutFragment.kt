package com.mwaghavul.mwaghavullexicon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        val aboutTextView: TextView = view.findViewById(R.id.about_text)
        aboutTextView.text = aboutText
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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