package com.example.usfitness.database.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(indices = [Index(value = ["mobile"], unique = true)] )
data class Customer(
    @PrimaryKey @ColumnInfo(name = "cid") val cid: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "mobile") val mobile: String,
    @ColumnInfo(name = "join_date") val joinDate: LocalDate,
)