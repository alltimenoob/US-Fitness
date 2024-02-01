package com.example.usfitness.database.record

import androidx.room.ColumnInfo
import java.time.LocalDate

data class DebtInfo(
    @ColumnInfo(name = "rid") val rid : Int,
    @ColumnInfo(name = "end_date") val endDate: LocalDate,
    @ColumnInfo(name = "sum(Payment.amount)-Record.total") val debt: Int,
)
