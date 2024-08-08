package com.example.mwaghavullexicon

import Word
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "mwaghavul_dic.db"
        private const val DATABASE_VERSION = 1
        private const val ENG_ENG_TABLE = "eng_dic"
        private const val MWA_ENG_TABLE = "mwa_eng"
        private const val ENG_MWA_TABLE = "eng_mwa"
        private const val BOOKMARK_TABLE = "bookmark"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TERM = "term"
        private const val COLUMN_WORD = "word"
        private const val COLUMN_TRANSLATION = "translation"
        private const val COLUMN_DEFINITION = "definition"
        private const val COLUMN_LANGUAGE = "language"
        private const val COLUMN_AUDIO = "audio"
        private const val COLUMN_NOTE = "note"
        private const val COLUMN_PRONUNCIATION = "pronunciation"
        private const val COLUMN_MWAGHAVUL = "mwaghavul"
        private const val COLUMN_PL = "pl"
        private const val COLUMN_POS = "pos"
        private const val COLUMN_IPA = "ipa"
        private const val COLUMN_GLOSS = "gloss"
        private const val COLUMN_EXAMPLES = "examples"

    }
    private val DATABASE_LOCATION = "data/data/${context.packageName}/databases/"
    private val DATABASE_FULL_PATH = "$DATABASE_LOCATION$DATABASE_NAME"
    lateinit var db : SQLiteDatabase

    init {
        if (!databaseExists()) {
            try {
                val dbLocation:File = File(DATABASE_LOCATION)
                dbLocation.mkdirs()
                extractAssetToDatabaseDirectory(DATABASE_NAME)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        db = SQLiteDatabase.openOrCreateDatabase(DATABASE_FULL_PATH, null)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createEngMwaTable = "CREATE TABLE IF NOT EXISTS $ENG_MWA_TABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MWAGHAVUL TEXT," +
                "$COLUMN_PL TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_IPA TEXT," +
                "$COLUMN_GLOSS TEXT," +
                "$COLUMN_EXAMPLES TEXT)"
        val createMwaEngTable = "CREATE TABLE IF NOT EXISTS $MWA_ENG_TABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MWAGHAVUL TEXT," +
                "$COLUMN_PL TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_IPA TEXT," +
                "$COLUMN_GLOSS TEXT," +
                "$COLUMN_EXAMPLES TEXT)"
        val createEngEngTable = "CREATE TABLE IF NOT EXISTS $ENG_ENG_TABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_WORD TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_DEFINITION TEXT," +
                "$COLUMN_EXAMPLES TEXT)"

        db.execSQL(createEngMwaTable)
        db.execSQL(createMwaEngTable)
        db.execSQL(createEngEngTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $ENG_MWA_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $MWA_ENG_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $ENG_ENG_TABLE")
        onCreate(db)
    }

    fun getAllWords(dicType: Int): List<Word> {
        val words = mutableListOf<Word>()
        val tableName = try {
            getTableName(dicType)
        } catch (e: IllegalArgumentException) {
            Log.e("DBHelper", "Invalid dictionary type: $dicType")
            return words
        }

        val cursor = db.rawQuery("SELECT * FROM $tableName ", null)
        if (cursor.moveToFirst()) {
            do {
                when (dicType) {
                    R.id.mwaghavul_english -> {
                        val word = Word(
                            term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                            pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                            pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                            pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                            translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                            examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                        )
                        words.add(word)
                    } R.id.english_mwaghavul -> {
                    val word = Word(
                        translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                        pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                        pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                        term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                        examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                    words.add(word)
                    } R.id.english_english -> {
                        val word = Word(
                            term =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)),
                            pos =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                            definition =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                            examples =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                        )
                        words.add(word)
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }

    fun searchWord(dicType: Int, query: String): Word? {
        var word : Word? = null
        val db = this.readableDatabase
        val tableName = getTableName(dicType)
        val columnName = getColumnName(dicType)

        val cursor = db.rawQuery("SELECT * FROM $tableName WHERE upper([$columnName]) = upper('$query');", null)
        if (cursor.moveToFirst()) {
            do {
                when (dicType) {
                    R.id.mwaghavul_english -> {
                        word = Word(
                            term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                            pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                            pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                            pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                            translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                            examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                        )
                    } R.id.english_mwaghavul -> {
                        word = Word(
                        translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                        pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                        pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                        term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                        examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                    } R.id.english_english -> {
                    word = Word(
                        term =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)),
                        pos =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        definition =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                        examples =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return word
    }

    fun getTableName(dicType: Int): String {
        var tableName = ""
        if (dicType == R.id.mwaghavul_english){
            tableName = MWA_ENG_TABLE
        }else if (dicType == R.id.english_mwaghavul){
            tableName = ENG_MWA_TABLE
        }else if (dicType == R.id.english_english){
            tableName = ENG_ENG_TABLE
        }
        return tableName
    }

    private fun getColumnName(dicType: Int): String {
        return when (dicType) {
            R.id.mwaghavul_english -> COLUMN_MWAGHAVUL
            R.id.english_mwaghavul -> COLUMN_GLOSS
            R.id.english_english -> COLUMN_WORD
            else -> throw IllegalAccessException("Invalid dictionary type")
        }
    }

    fun addBookmark(word: Word, tableName: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_TERM, word.term)
        contentValues.put(COLUMN_PL, word.pl)
        contentValues.put(COLUMN_POS, word.pos)
        contentValues.put(COLUMN_PRONUNCIATION, word.pronunciation)
        contentValues.put(COLUMN_DEFINITION, word.definition)
        contentValues.put(COLUMN_EXAMPLES, word.examples)
        contentValues.put(COLUMN_TRANSLATION, word.translation)
        contentValues.put(COLUMN_AUDIO, word.audio)
        contentValues.put(COLUMN_LANGUAGE, word.language)
        contentValues.put(COLUMN_NOTE, word.note)

        try {
            db.insert(BOOKMARK_TABLE, null, contentValues)
        }catch (e: Exception) {
            // Handle the exception
            Log.e("Error", "Error inserting word into bookmark: ${e.message}")
        }
        db.close()
    }

    fun removeBookmark(word: Word, tableName: String) {
        val db = this.writableDatabase
        try {
            db.delete(BOOKMARK_TABLE, "$COLUMN_TERM = ?", arrayOf(word.term))
        }catch (e: Exception) {
            // Handle the exception
            Log.e("Error", "Error deleting word from bookmark: ${e.message}")
        }
        db.close()
    }

    fun isBookmarked(word: Word, tableName: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $BOOKMARK_TABLE WHERE $COLUMN_TERM = ?;"

        val cursor = db.rawQuery(query, arrayOf(word.term))
        val isBookmarked = cursor.count > 0
        cursor.close()

        return isBookmarked
    }

    fun isWordInBookmark(word: Word): Boolean {
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $BOOKMARK_TABLE WHERE upper($COLUMN_TERM) = upper(?) AND upper($COLUMN_POS) = upper(?) AND upper($COLUMN_PRONUNCIATION) = upper(?) AND upper($COLUMN_DEFINITION) = upper(?) AND upper($COLUMN_EXAMPLES) = upper(?) AND upper($COLUMN_TRANSLATION) = upper(?);",
            arrayOf(word.term.uppercase(), word.pos.uppercase(), word.pronunciation.uppercase(), word.definition.uppercase(), word.examples.uppercase(), word.translation.uppercase()))
        val isFound = cursor.moveToFirst()
        cursor.close()
        db.close()
        return isFound
    }

    fun getAllWordsFromBookmark(): List<Word> {
        val words = mutableListOf<Word>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $BOOKMARK_TABLE ORDER BY [date] DESC;", null)
        if (cursor.moveToFirst()) {
            do {
                val word = Word(
                    term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TERM)),
                    pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                    pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(
                        COLUMN_PRONUNCIATION)),
                    translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATION)),
                    definition =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                    examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                )
                words.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }

    fun getWordFromBookmark(query: String): Word? {
        var word: Word? = null
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $BOOKMARK_TABLE WHERE upper([$COLUMN_TERM]) = upper('$query');", null)
        if (cursor.moveToFirst()) {
            word = Word(
                term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TERM)),
                pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRONUNCIATION)),
                definition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES)),
                translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATION)),
                audio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUDIO)),
                language = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE)),
                note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
            )
        }
        cursor.close()
        db.close()
        return word
    }

    fun databaseExists(): Boolean {
        val dbFile = File(DATABASE_FULL_PATH)
        return dbFile.exists()
    }

    fun extractAssetToDatabaseDirectory(fileName: String) {
        val inputStream = context.assets.open(fileName)
        val outputFile = File(DATABASE_FULL_PATH)
        val outputStream = FileOutputStream(outputFile)

        val buffer = ByteArray(8192)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

}
