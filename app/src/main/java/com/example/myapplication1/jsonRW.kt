package com.example.myapplication1

import Event
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun eventSetReader(fileName: String): SortedSet<Event> {
    val reader = File(fileName)
    val eventsInJson: String

    try {
        if (reader.exists()) {
            eventsInJson = reader.readText()
        } else {
            return sortedSetOf<Event>()
        }
    } catch (e: Exception) {
        return sortedSetOf<Event>()
    }

    val builder = GsonBuilder()
    builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    val gson = builder.create()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type

    return try {
        gson.fromJson(eventsInJson, eventSortedSetTypeToken)
    } catch (e: Exception) {
        sortedSetOf<Event>()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun eventSetWriter(fileName: String, events: SortedSet<Event>) {
    val writer = File(fileName)
    val eventsInJson: String

    val builder = GsonBuilder()
    builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    val gson = builder.create()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type

    eventsInJson = gson.toJson(events, eventSortedSetTypeToken)

    writer.createNewFile()
    writer.writeText(eventsInJson)
}