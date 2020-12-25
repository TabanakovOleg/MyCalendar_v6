package com.example.myapplication1

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime

class LocalDateTimeAdapter: JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("year", src.year)
        jsonObject.addProperty("month", src.monthValue)
        jsonObject.addProperty("day", src.dayOfMonth)
        jsonObject.addProperty("hour", src.hour)
        jsonObject.addProperty("minute", src.minute)
        return jsonObject
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        val jsonObject: JsonObject = json.asJsonObject

        var dateString = ""

        val year: Int = jsonObject.get("year").asInt
        dateString += "$year-"

        val month: Int = jsonObject.get("month").asInt
        if (month < 10) dateString += "0"
        dateString += "$month-"

        val day: Int = jsonObject.get("day").asInt
        if (day < 10) dateString += "0"
        dateString += "${day}T"

        val hour: Int = jsonObject.get("hour").asInt
        if (hour < 10) dateString += "0"
        dateString += "$hour:"

        val minute: Int = jsonObject.get("minute").asInt
        if (minute < 10) dateString += "0"
        dateString += "$minute:00"

        return  LocalDateTime.parse(dateString)
    }
}