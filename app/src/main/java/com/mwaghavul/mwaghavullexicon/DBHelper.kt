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
        private const val DATABASE_VERSION = 2 
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

    init {
        ensureDatabaseSetup()
    }

    private fun ensureDatabaseSetup() {
        if (!databaseExists() || !databaseContainsWords()) {
            Log.d("DBHelper", "Database missing or empty. Copying from assets.")
            try {
                val dbLocation = File(databaseLocation)
                if (!dbLocation.exists()) dbLocation.mkdirs()
                extractAssetToDatabaseDirectory()
            } catch (e: IOException) {
                Log.e("DBHelper", "Error copying database", e)
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        createIndexes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            createIndexes(db)
        }
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $ENG_MWA_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_MWAGHAVUL TEXT, $COLUMN_PL TEXT, $COLUMN_POS TEXT, $COLUMN_IPA TEXT, $COLUMN_GLOSS TEXT, $COLUMN_EXAMPLES TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $MWA_ENG_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_MWAGHAVUL TEXT, $COLUMN_PL TEXT, $COLUMN_POS TEXT, $COLUMN_IPA TEXT, $COLUMN_GLOSS TEXT, $COLUMN_EXAMPLES TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $ENG_ENG_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_WORD TEXT, $COLUMN_POS TEXT, $COLUMN_DEFINITION TEXT, $COLUMN_EXAMPLES TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $BOOKMARK_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TERM TEXT, $COLUMN_PL TEXT, $COLUMN_POS TEXT, $COLUMN_IPA TEXT, $COLUMN_DEFINITION TEXT, $COLUMN_EXAMPLES TEXT, $COLUMN_TRANSLATION TEXT, $COLUMN_AUDIO TEXT, $COLUMN_LANGUAGE TEXT, $COLUMN_NOTE TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $HISTORY_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TERM TEXT, $COLUMN_PL TEXT, $COLUMN_POS TEXT, $COLUMN_IPA TEXT, $COLUMN_DEFINITION TEXT, $COLUMN_EXAMPLES TEXT, $COLUMN_TRANSLATION TEXT, $COLUMN_AUDIO TEXT, $COLUMN_LANGUAGE TEXT, $COLUMN_NOTE TEXT)")
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mwa_eng_term ON $MWA_ENG_TABLE ($COLUMN_MWAGHAVUL)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eng_mwa_term ON $ENG_MWA_TABLE ($COLUMN_GLOSS)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eng_eng_term ON $ENG_ENG_TABLE ($COLUMN_WORD)")
    }

    private fun getDicTypeKey(): String {
        val savedValue = Global.getState(context, SELECTED_DICTIONARY_KEY) ?: DIC_ENG_MWA
        
        val savedInt = savedValue.toIntOrNull()
        if (savedInt != null) {
            return when (savedInt) {
                R.id.mwaghavul_english -> DIC_MWA_ENG
                R.id.english_mwaghavul -> DIC_ENG_MWA
                R.id.english_english -> DIC_ENG_ENG
                else -> DIC_ENG_MWA
            }
        }
        
        return when (savedValue) {
            DIC_MWA_ENG, DIC_ENG_MWA, DIC_ENG_ENG -> savedValue
            else -> DIC_ENG_MWA
        }
    }

    fun getWords(limit: Int, offset: Int): MutableList<Word> {
        val words = mutableListOf<Word>()
        val dicKey = getDicTypeKey()
        val tableName = getTableName(dicKey)

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName LIMIT ? OFFSET ?", arrayOf(limit.toString(), offset.toString()))
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(extractWordFromCursor(dicKey, cursor))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error getting words", e)
        } finally {
            cursor.close()
        }
        return words
    }

    fun searchWords(query: String, limit: Int, offset: Int): MutableList<Word> {
        val words = mutableListOf<Word>()
        val dicKey = getDicTypeKey()
        val tableName = getTableName(dicKey)
        val columnName = getColumnName(dicKey)

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName WHERE $columnName LIKE ? LIMIT ? OFFSET ?", 
            arrayOf("$query%", limit.toString(), offset.toString()))
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(extractWordFromCursor(dicKey, cursor))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error searching words", e)
        } finally {
            cursor.close()
        }
        return words
    }

    private fun extractWordFromCursor(dicKey: String, cursor: Cursor): Word {
        return when (dicKey) {
            DIC_MWA_ENG -> Word(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
            )
            DIC_ENG_MWA -> Word(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                translation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
            )
            DIC_ENG_ENG -> Word(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                term = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORD)),
                pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                definition = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFINITION)),
                examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
            )
            else -> Word(id = "0", term = "Unknown")
        }
    }

    private fun getTableName(dicKey: String): String {
        return when (dicKey) {
            DIC_MWA_ENG -> MWA_ENG_TABLE
            DIC_ENG_MWA -> ENG_MWA_TABLE
            DIC_ENG_ENG -> ENG_ENG_TABLE
            else -> ENG_MWA_TABLE
        }
    }

    private fun getColumnName(dicKey: String): String {
        return when (dicKey) {
            DIC_MWA_ENG -> COLUMN_MWAGHAVUL
            DIC_ENG_MWA -> COLUMN_GLOSS
            DIC_ENG_ENG -> COLUMN_WORD
            else -> COLUMN_GLOSS
        }
    }

    fun addWordToTable(word: Word, tableName: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            val numericId = word.id.toIntOrNull()
            if (numericId != null) {
                put(COLUMN_ID, numericId)
            }
            put(COLUMN_TERM, word.term)
            put(COLUMN_PL, word.pl)
            put(COLUMN_POS, word.pos)
            put(COLUMN_IPA, word.pronunciation)
            put(COLUMN_DEFINITION, word.definition)
            put(COLUMN_EXAMPLES, word.examples)
            put(COLUMN_TRANSLATION, word.translation)
            put(COLUMN_AUDIO, word.audio)
            put(COLUMN_LANGUAGE, word.language)
            put(COLUMN_NOTE, System.currentTimeMillis().toString())
        }
        db.replace(tableName, null, contentValues)
    }

    fun removeFromTable(word: Word, tableName: String) {
        val db = this.writableDatabase
        db.delete(tableName, "$COLUMN_ID = ? AND $COLUMN_TERM = ?", arrayOf(word.id, word.term))
    }

    fun isBookmarked(word: Word): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $BOOKMARK_TABLE WHERE $COLUMN_ID = ? AND $COLUMN_TERM = ?", arrayOf(word.id, word.term))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllWordsFromTable(tableName: String): List<Word> {
        val words = mutableListOf<Word>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName ORDER BY $COLUMN_NOTE DESC", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(Word(
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
                    ))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error getting all from table", e)
        } finally {
            cursor.close()
        }
        return words
    }

    private fun databaseExists(): Boolean = File(databaseFullPath).exists()

    private fun databaseContainsWords(): Boolean {
        return try {
            val db = SQLiteDatabase.openDatabase(databaseFullPath, null, SQLiteDatabase.OPEN_READONLY)
            val cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name IN (?, ?, ?)", 
                arrayOf(ENG_MWA_TABLE, MWA_ENG_TABLE, ENG_ENG_TABLE))
            val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()
            db.close()
            count > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun extractAssetToDatabaseDirectory() {
        context.assets.open(DATABASE_NAME).use { input ->
            FileOutputStream(databaseFullPath).use { output ->
                input.copyTo(output)
            }
        }
    }

    fun getHistoryCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $HISTORY_TABLE", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun deleteOldestEntries(count: Int) {
        if (count > 0) {
            this.writableDatabase.execSQL("DELETE FROM $HISTORY_TABLE WHERE note IN (SELECT note FROM $HISTORY_TABLE ORDER BY note ASC LIMIT ?)", arrayOf(count))
        }
    }

    fun getRandomWord(): Word {
        val tableNames = listOf(MWA_ENG_TABLE, ENG_MWA_TABLE, ENG_ENG_TABLE)
        val randomTable = tableNames.random()
        val dicKey = when(randomTable) {
            MWA_ENG_TABLE -> DIC_MWA_ENG
            ENG_MWA_TABLE -> DIC_ENG_MWA
            else -> DIC_ENG_ENG
        }
        
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $randomTable ORDER BY RANDOM() LIMIT 1", null)
        try {
            if (cursor.moveToFirst()) {
                return extractWordFromCursor(dicKey, cursor)
            }
        } finally {
            cursor.close()
        }
        throw RuntimeException("Database empty")
    }
}
