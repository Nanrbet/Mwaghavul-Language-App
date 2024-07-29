data class Word(
    val mwaghavul: String,
    val pl: String? = null,
    val pos: String? = null,
    val ipa: String? = null,
    val gloss: String,
    val examples: String? = null
) {
    companion object {
        // Factory method for Mwaghavul to English
        fun fromMwaghavul(mwaghavul: String, pl: String?, pos: String?, ipa: String?, gloss: String, examples: String?): Word {
            return Word(mwaghavul, pl, pos, ipa, gloss, examples)
        }

        // Factory method for English to Mwaghavul
        fun fromEnglishToMwaghavul(gloss: String, mwaghavul: String, pl: String?, pos: String?, ipa: String?, examples: String?): Word {
            return Word(mwaghavul, pl, pos, ipa, gloss, examples)
        }

        // Factory method for English to English
        fun fromEnglish(gloss: String, pl: String?, pos: String?, ipa: String?, examples: String?): Word {
            return Word(mwaghavul = "", pl = pl, pos = pos, ipa = ipa, gloss = gloss, examples = examples)
        }
    }
}
