import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    val id: String = "",
    val term: String= "",
    val pl: String = "",
    val pos: String = "",
    val pronunciation: String = "",
    val definition: String = "",
    val examples: String = "",
    val translation: String = "",
    val audio: String = "",
    val language: String = "",
    val note: String = ""
) : Parcelable
