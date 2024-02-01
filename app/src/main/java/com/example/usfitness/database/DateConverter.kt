package com.example.usfitness.database

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverter {
    @TypeConverter
    fun toDate(dateText: String?): LocalDate {
        return LocalDate.parse(dateText)
    }
    @TypeConverter
    fun toTimestamp(date: LocalDate?): String {
        return date.toString()
    }
}