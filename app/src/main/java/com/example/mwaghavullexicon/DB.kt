package com.example.mwaghavullexicon

import Word
import android.content.Context
import android.widget.Toast
import java.io.Serializable

object DB {
//    private lateinit var dbHelper: DBHelper
//    // Initialize the DBHelper instance
//    fun initialize(context: Context) {
//        dbHelper = DBHelper(context)
//    }
//     public fun getData(itemId: Int): Any {
//         if (!::dbHelper.isInitialized) {
//             throw IllegalStateException("DBHelper is not initialized. Call initialize() before accessing data.")
//         }
//
//         return when (itemId) {
//
//            R.id.mwaghavul_english -> {
//                getMwaghavulEng()
//            }
//            R.id.english_mwaghavul -> {
//                getEngMwaghavul()
//            }
//            R.id.english_english -> {
//                getEngEng()
//            }
//            else -> getEngMwaghavul()
//        }
//    }
//
//    public fun getEngMwaghavul(): List<Word> {
//        val source = dbHelper.getAllWords(R.id.english_mwaghavul)
////        val source = arrayOf(
////            "apple - cing", "banana - ndok", "cherry - kish", "date - ndom", "elderberry - nyit",
////            "fig - king", "grape - mpis", "honeydew - ng'ah", "kiwi - pish", "lemon - lor",
////            "mango - kut", "nectarine - kung", "orange - pof", "papaya - lat", "quince - jin",
////            "raspberry - twas", "strawberry - ngir", "tangerine - pak", "ugli - ngot",
////            "vanilla - zhing", "watermelon - juom", "xigua - xuan", "yam - tuk", "zucchini - kwar",
////            "apricot - dom", "blueberry - pum", "cantaloupe - ton", "dragonfruit - shrong",
////            "eggplant - rok"
////        )
//        return source
//    }
//    public fun getMwaghavulEng(): Array<String> {
//        val source = arrayOf(
//            "cing - apple", "ndok - banana", "kish - cherry", "ndom - date", "nyit - elderberry",
//            "king - fig", "mpis - grape", "ng'ah - honeydew", "pish - kiwi", "lor - lemon",
//            "kut - mango", "kung - nectarine", "pof - orange", "lat - papaya", "jin - quince",
//            "twas - raspberry", "ngir - strawberry", "pak - tangerine", "ngot - ugli",
//            "zhing - vanilla", "juom - watermelon", "xuan - xigua", "tuk - yam", "kwar - zucchini",
//            "dom - apricot", "pum - blueberry", "ton - cantaloupe", "shrong - dragonfruit",
//            "rok - eggplant"
//        )
//        return source
//    }
//    public fun getEngEng(): List<Word> {
//        val source = dbHelper.getAllWords(R.id.english_english)
////        val source = arrayOf(
////            "apple - a fruit", "banana - a long yellow fruit", "cherry - a small red fruit", "date - a sweet fruit",
////            "elderberry - a berry from the elder tree", "fig - a soft pear-shaped fruit", "grape - a small round fruit",
////            "honeydew - a type of melon", "kiwi - a small brown fruit with green flesh", "lemon - a sour yellow fruit",
////            "mango - a tropical fruit", "nectarine - a smooth-skinned peach", "orange - a citrus fruit",
////            "papaya - a tropical fruit with orange flesh", "quince - a hard yellow fruit", "raspberry - a small red fruit",
////            "strawberry - a red juicy fruit", "tangerine - a small orange citrus fruit", "ugli - a hybrid citrus fruit",
////            "vanilla - a flavoring derived from orchids", "watermelon - a large green fruit with red flesh",
////            "xigua - another term for watermelon", "yam - a starchy tuber", "zucchini - a green summer squash",
////            "apricot - a small orange fruit", "blueberry - a small blue fruit", "cantaloupe - a type of melon",
////            "dragonfruit - a tropical fruit with a scaly skin", "eggplant - a purple vegetable"
////        )
//        return source
//    }
}