package com.example.usfitness.database.record

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordsDAO {
    @Query("SELECT * FROM record")
    suspend fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE cid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Record>>

    @Query("SELECT * FROM record INNER JOIN customer ON record.cid == customer.cid WHERE first_name LIKE (:firstName)")
    fun findByName(firstName: String): LiveData<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg record: Record)

    @Delete
    fun delete(record: Record)
}