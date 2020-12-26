package com.example.myapplication1

import Event
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun eventSetReader(fileInputStream: FileInputStream): SortedSet<Event> {
    val eventsInJson = BufferedReader(InputStreamReader(fileInputStream)).readLine()
    val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type

    return try {
        gson.fromJson(eventsInJson, eventSortedSetTypeToken)
    } catch (e: Exception) {
        sortedSetOf<Event>()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun eventSetWriter(fileOutputStream: FileOutputStream, events: SortedSet<Event>) {
    val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
    val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type
    val eventsInJson = gson.toJson(events, eventSortedSetTypeToken)

    fileOutputStream.write(eventsInJson.toByteArray())
}