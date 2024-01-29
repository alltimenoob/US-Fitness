package com.example.usfitness.database

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: String?): LocalDate? {
        return LocalDate.parse(timestamp)
    }
    @TypeConverter
    fun toTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}