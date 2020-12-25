
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime


class Event(var date: LocalDateTime, var title: String, var description:String): Comparable<Event> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun toString(): String {
        return "${date.year}/${date.monthValue}/${date.dayOfMonth} ${date.hour}:${date.minute}: $title \n"
    }

    override operator fun compareTo(other: Event): Int {
        if (this.date > other.date) return 1
        if (this.date < other.date) return -1
        if (this.title == other.title && this.date == other.date) return 0
        return 1
    }
}