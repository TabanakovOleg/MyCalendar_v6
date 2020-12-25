package com.example.myapplication1

import Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

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

    val gson = Gson()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type

    return try {
        gson.fromJson(eventsInJson, eventSortedSetTypeToken)
    } catch (e: Exception) {
        sortedSetOf<Event>()
    }
}

fun eventSetWriter(fileName: String, events: SortedSet<Event>) {
    val writer = File(fileName)
    val eventsInJson: String

    val gson = Gson()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type

    eventsInJson = gson.toJson(events, eventSortedSetTypeToken)

    writer.createNewFile()
    writer.writeText(eventsInJson)
}