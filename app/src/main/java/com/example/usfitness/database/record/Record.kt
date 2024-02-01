package com.example.usfitness.database.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.usfitness.database.customer.Customer
import java.time.LocalDate

@Entity(
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["cid"],
        childColumns = ["cid"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Record(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rid") val rid: Int?,
    @ColumnInfo(name = "cid") val cid: Int,
    @ColumnInfo(name = "start_date") val startDate : LocalDate,
    @ColumnInfo(name = "end_date") val endDate : LocalDate,
    @ColumnInfo(name = "total") val total : Int,
)