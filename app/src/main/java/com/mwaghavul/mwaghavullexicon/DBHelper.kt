package com.mwaghavul.mwaghavullexicon

import Word
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val BOOKMARK_TABLE = "bookmark_table"
const val HISTORY_TABLE = "history_table"

class DBHelper(private val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "mwaghavul_dic.db"
        private const val DATABASE_VERSION = 1
        private const val ENG_ENG_TABLE = "eng_dic"
        private const val MWA_ENG_TABLE = "mwa_eng"
        private const val ENG_MWA_TABLE = "eng_mwa"
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
    private val databaseLocation = "data/data/${context.packageName}/databases/"
    private val databaseFullPath = "$databaseLocation$DATABASE_NAME"
    private var db : SQLiteDatabase

    init {
        if (!databaseExists()) {
            Log.d("DBHelper", "Database does not exist. Creating database.")
            try {
                val dbLocation = File(databaseLocation)
                dbLocation.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (!databaseContainsWords()) {
            Log.d("DBHelper", "Database does not contain words. Copying data from assets.")
            try {
                extractAssetToDatabaseDirectory()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        db = SQLiteDatabase.openOrCreateDatabase(databaseFullPath, null)
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
        val createBookmarkTable = "CREATE TABLE IF NOT EXISTS $BOOKMARK_TABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TERM TEXT," +
                "$COLUMN_PL TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_IPA TEXT," +
                "$COLUMN_DEFINITION TEXT," +
                "$COLUMN_EXAMPLES TEXT," +
                "$COLUMN_TRANSLATION TEXT," +
                "$COLUMN_AUDIO TEXT," +
                "$COLUMN_LANGUAGE TEXT," +
                "$COLUMN_NOTE TEXT)"
        val createHistoryTable = "CREATE TABLE IF NOT EXISTS $HISTORY_TABLE (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TERM TEXT," +
                "$COLUMN_PL TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_IPA TEXT," +
                "$COLUMN_DEFINITION TEXT," +
                "$COLUMN_EXAMPLES TEXT," +
                "$COLUMN_TRANSLATION TEXT," +
                "$COLUMN_AUDIO TEXT," +
                "$COLUMN_LANGUAGE TEXT," +
                "$COLUMN_NOTE TEXT)"

        db.execSQL(createEngMwaTable)
        db.execSQL(createMwaEngTable)
        db.execSQL(createEngEngTable)
        db.execSQL(createBookmarkTable)
        db.execSQL(createHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $ENG_MWA_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $MWA_ENG_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $ENG_ENG_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $BOOKMARK_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $HISTORY_TABLE")
        onCreate(db)
    }

    fun getWords(limit: Int, offset: Int): MutableList<Word> {
        val words = mutableListOf<Word>()
        db = this.readableDatabase
        val dicType = getDicType()
        val tableName = try {
            getTableName(dicType)
        } catch (e: IllegalArgumentException) {
            Log.e("DBHelper", "Invalid dictionary type: ")
            return words
        }
        // Check if tableName is empty
        if (tableName.isEmpty()) {
            Log.e("DBHelper", "Table name is empty.")
            return words
        }

        // Pagination Logic
        val cursor = db.rawQuery("SELECT * FROM $tableName LIMIT $limit OFFSET $offset", null)
        Log.d("DBHelper", "Query executed: SELECT * FROM $tableName LIMIT $limit OFFSET $offset;")
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(extractWordFromCursor(dicType, cursor))
                } while (cursor.moveToNext())
            } else {
                Log.d("DBHelper", "No words found in table: $tableName")
            }
        } finally {
            cursor.close() // Always close the cursor
            db.close()     // Close the database
        }
        return words
    }

    private fun extractWordFromCursor(dicType: Int, cursor: Cursor): Word {
        return when (dicType) {
            R.id.mwaghavul_english -> {
                val word = Word(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                    pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                    pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                    translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                    examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                )
                return word
            }
            R.id.english_mwaghavul -> {
                val word = Word(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                    pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                    pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                    term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                    examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                )
                return word
            }
            R.id.english_english -> {
                val word = Word(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    term =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)),
                    pos =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    definition =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                    examples =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                )
                return word
            }
            else -> Word(
                id = "no id",
                term = "no term",
                pl = "no pl",
                pos = "no pos",
                pronunciation = "no pronunciation",
                definition = "no definition",
                examples = "no examples",
                translation = "no translation",
                audio = "no audio",
                language = "no language",
                note = "no note"
            )
        }
    }


    fun searchWords(query: String, limit: Int, offset: Int): MutableList<Word> {
        val words = mutableListOf<Word>()
        db = this.readableDatabase
        val dicType = getDicType()
        val tableName = getTableName(dicType)
        val columnName = getColumnName()

        val normalizedQuery = query.replace(" ", "").uppercase() + "%"

        val cursor = db.rawQuery("SELECT * FROM $tableName WHERE REPLACE(UPPER($columnName), ' ', '') COLLATE NOACCENTS LIKE upper('$normalizedQuery%') LIMIT $limit OFFSET $offset;", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(extractWordFromCursor(dicType, cursor))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
            db.close()
        }
        return words
    }

    private fun getTableName(dicType: Int): String {
        var tableName = ENG_MWA_TABLE
        Log.d("FATAL", "tableName $tableName")
        when (dicType) {
            R.id.mwaghavul_english -> {
                tableName = MWA_ENG_TABLE
            }
            R.id.english_mwaghavul -> {
                tableName = ENG_MWA_TABLE
            }
            R.id.english_english -> {
                tableName = ENG_ENG_TABLE
            }
        }
        return tableName
    }

    private fun getColumnName(): String {
        return when (getDicType()) {
            R.id.mwaghavul_english -> COLUMN_MWAGHAVUL
            R.id.english_mwaghavul -> COLUMN_GLOSS
            R.id.english_english -> COLUMN_WORD
            else -> throw IllegalAccessException("Invalid dictionary type")
        }
    }

    fun addWordToTable(word: Word, tableName: String) {
        db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_ID, word.id)
        contentValues.put(COLUMN_TERM, word.term)
        contentValues.put(COLUMN_PL, word.pl)
        contentValues.put(COLUMN_POS, word.pos)
        contentValues.put(COLUMN_IPA, word.pronunciation)
        contentValues.put(COLUMN_DEFINITION, word.definition)
        contentValues.put(COLUMN_EXAMPLES, word.examples)
        contentValues.put(COLUMN_TRANSLATION, word.translation)
        contentValues.put(COLUMN_AUDIO, word.audio)
        contentValues.put(COLUMN_LANGUAGE, word.language)
        contentValues.put(COLUMN_NOTE, System.currentTimeMillis().toString())

        try {
            db.insert(tableName, null, contentValues)
        } catch (e: Exception) {
            Log.e("db Error", "Error inserting word into table $tableName: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun removeFromTable(word: Word, tableName: String) {
        db = this.writableDatabase
        try {
            db.delete(tableName, "$COLUMN_ID = ? AND $COLUMN_TERM = ?", arrayOf(word.id, word.term))
        }catch (e: Exception) {
            // Handle the exception
            Log.e("Error", "Error deleting word from $tableName: ${e.message}")
        }
        db.close()
    }

    fun isBookmarked(word: Word): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $BOOKMARK_TABLE WHERE $COLUMN_ID = ? AND $COLUMN_TERM = ?;"

        val cursor = db.rawQuery(query, arrayOf(word.id, word.term))
        val isBookmarked = cursor.count > 0
        cursor.close()

        return isBookmarked
    }

    fun getAllWordsFromTable(tableName: String): List<Word> {
        val words = mutableListOf<Word>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $tableName ORDER BY $COLUMN_NOTE DESC;", null)
        if (cursor.moveToFirst()) {
            do {
                val word = Word(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TERM)),
                    pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                    pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                    definition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                    examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES)),
                    translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATION)),
                    audio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUDIO)),
                    language = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
                )
                words.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }


    private fun databaseExists(): Boolean {
        val dbFile = File(databaseFullPath)
        return dbFile.exists()
    }

    private fun databaseContainsWords(): Boolean {
        var totalRowCount = 0

        val db = SQLiteDatabase.openOrCreateDatabase(databaseFullPath, null)
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name != 'android_metadata'", null)

        while (cursor.moveToNext()) {
            val tableName = cursor.getString(0)
            val countCursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)

            if (countCursor.moveToFirst()) {
                val rowCount = countCursor.getInt(0)
                totalRowCount += rowCount
            }

            countCursor.close()
        }

        cursor.close()
        db.close()

        return totalRowCount > 0
    }

    private fun extractAssetToDatabaseDirectory() {
        val inputStream = context.assets.open(DATABASE_NAME)
        val outputFile = File(databaseFullPath)
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
    private fun getDicType(): Int {
        return Global.getState(context, SELECTED_DICTIONARY_KEY)?.toIntOrNull() ?: R.id.english_mwaghavul
    }

    // For History Fragment
    fun getHistoryCount(): Int {
        val countQuery = "SELECT COUNT(*) FROM history_table"
        this.readableDatabase.use { db ->
            val cursor = db.rawQuery(countQuery, null)
            cursor.use {
                if (it.moveToFirst()) {
                    return it.getInt(0)
                }
            }
        }
        return 0
    }

    fun deleteOldestEntries(numberOfEntriesToDelete: Int) {
        // Deleting the oldest entries, Only run if the number is positive
        if (numberOfEntriesToDelete > 0) {
            this.writableDatabase.use { db ->
                db.execSQL("DELETE FROM history_table WHERE note IN (SELECT note FROM history_table ORDER BY note ASC LIMIT ?)", arrayOf(numberOfEntriesToDelete))
            }
        }
    }

    // For Home Fragment
    fun getRandomWord(): Word {
        // Step 1: Define the list of table names
        val tableNames = listOf(
            MWA_ENG_TABLE,
            ENG_MWA_TABLE,
            ENG_ENG_TABLE
        )

        // Randomly select a table name
        val randomTableName = tableNames.random()

        // Query to select a random word from the selected table
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $randomTableName ORDER BY RANDOM() LIMIT 1;", null)

        if (cursor.moveToFirst()) {
            return when (randomTableName) {
                MWA_ENG_TABLE -> {
                    Word(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                        pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                        pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                        translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                        examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                }
                ENG_MWA_TABLE -> {
                    Word(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                        pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                        pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                        term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                        examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                }
                ENG_ENG_TABLE -> {
                    Word(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)),
                        pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                        definition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                        examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                    )
                }
                else -> throw IllegalArgumentException("Unexpected table name: $randomTableName")
            }
        }

        cursor.close()
        db.close()

        // If the cursor is empty, throw an exception
        throw RuntimeException("No words found in the selected RANDOM table, dbHelper")
    }
}
