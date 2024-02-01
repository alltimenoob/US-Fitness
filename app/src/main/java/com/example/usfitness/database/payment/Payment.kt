package com.example.usfitness.database.payment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import com.example.usfitness.database.record.Record

@Entity(
    foreignKeys = [ForeignKey(
        entity = Record::class,
        parentColumns = ["rid"],
        childColumns = ["rid"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pid") val pid: Int?,
    @ColumnInfo(name = "rid") val rid: Int,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
)