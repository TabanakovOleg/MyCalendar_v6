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

        val year: Int = jsonObject.get("year").asInt
        val month: Int = jsonObject.get("month").asInt
        val day: Int = jsonObject.get("day").asInt
        val hour: Int = jsonObject.get("hour").asInt
        val minute: Int = jsonObject.get("minute").asInt

        return  LocalDateTime.parse(buildDateString(year, month, day, hour, minute))
    }
}