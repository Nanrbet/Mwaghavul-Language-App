package com.example.mwaghavullexicon

import Word
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val DATABASE_LOCATION = "data/data/${context.packageName}/databases/"

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MWAGHAVUL TEXT," +
                "$COLUMN_PL TEXT," +
                "$COLUMN_POS TEXT," +
                "$COLUMN_IPA TEXT," +
                "$COLUMN_GLOSS TEXT," +
                "$COLUMN_EXAMPLES TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertWord(word: Word) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MWAGHAVUL, word.mwaghavul)
            put(COLUMN_PL, word.pl)
            put(COLUMN_POS, word.pos)
            put(COLUMN_IPA, word.ipa)
            put(COLUMN_GLOSS, word.gloss)
            put(COLUMN_EXAMPLES, word.examples)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllWords(): List<Word> {
        val words = mutableListOf<Word>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val word = Word(
                    mwaghavul = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MWAGHAVUL)),
                    pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PL)),
                    pos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POS)),
                    ipa = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IPA)),
                    gloss = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GLOSS)),
                    examples = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLES))
                )
                words.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }

    fun databaseExists(): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    fun extractAssetToDatabaseDirectory(fileName: String) {
        val inputStream = context.assets.open(fileName)
        val outputFile = File(DATABASE_LOCATION, DATABASE_NAME)
        val outputStream = FileOutputStream(outputFile)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    companion object {
        private const val DATABASE_NAME = "mwaghavul_dic.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "fullDictionary"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MWAGHAVUL = "mwaghavul"
        private const val COLUMN_PL = "pl"
        private const val COLUMN_POS = "pos"
        private const val COLUMN_IPA = "ipa"
        private const val COLUMN_GLOSS = "gloss"
        private const val COLUMN_EXAMPLES = "examples"
    }
}
