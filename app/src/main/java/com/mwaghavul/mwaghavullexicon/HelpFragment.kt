package com.mwaghavul.mwaghavullexicon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mwaghavul.mwaghavullexicon.R.*


class HelpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the help content
        val helpContent = view.findViewById<TextView>(R.id.help_content)
        helpContent.text = helpText
    }

    private val helpText: String
        get() = """
            Welcome to the Help Page!
            
            Welcome to the Mwaghavul Lexicon! This app is designed to help you explore and understand the rich vocabulary of the Mwaghavul language. Whether you're a learner or a fluent speaker, you'll find valuable resources here.
            
            Getting Started:
            1. Home Screen: Navigate to the main screen to access the vocabulary list.
            2. Search Functionality: Use the search bar at the top to find specific words.
            3. Word Details: Tap on any word to see its definition, pronunciation, and examples.
            4. Bookmarking: Save your favorite words by tapping the bookmark icon.
            
            Key Features:
            - Definitions: View detailed definitions for each word.
            - Pronunciation Guides: Listen to audio pronunciations for accurate learning.
            - Example Sentences: Read example sentences to see words in context.
            - Bookmarks: Easily access your saved words from the bookmarks section.
            - Rate Us: We appreciate your feedback! Please take a moment to rate us in the app store.
            
            Frequently Asked Questions:
            - Q: How can I search for a word?
              A: Simply type the word into the search bar at the top of the home screen.
            - Q: Can I suggest new words?
              A: Yes! Please contact us through the feedback section in the app.
            - Q: What if I encounter a bug?
              A: Report any issues via the feedback form, and we'll address them as soon as possible.
            
            Need More Help?
            If you have additional questions or need support, please reach out to us:
            - Email: support@mwaghavullexicon.com
                  
            Contact Support
            Email: nanribet.f@gmail.com
            Phone: +1 780 616 2027
            
            Thank you for using our app!
            """.trimIndent()
}
