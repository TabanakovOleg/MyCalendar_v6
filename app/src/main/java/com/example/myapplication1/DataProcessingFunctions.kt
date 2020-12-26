package com.example.myapplication1

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

fun getMonthName(month: Int): String =
    when(month) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "Doom"
    }

fun buildDateString(year: Int, month: Int, day: Int, hour: Int = 10, minute: Int = 10): String {
        var dateString = "$year-"
        if (month < 10) dateString += "0"; dateString += "$month-"
        if (day < 10) dateString += "0"; dateString += "${day}T"
        if (hour < 10) dateString += "0"; dateString += "$hour:"
        if (minute < 10) dateString += "0"; dateString += "$minute:00"
        return dateString
}

@RequiresApi(Build.VERSION_CODES.O)
fun areSameDays(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        if (date1.year == date2.year &&
            date1.monthValue == date2.monthValue &&
            date1.dayOfMonth == date2.dayOfMonth) return true
        return false
}