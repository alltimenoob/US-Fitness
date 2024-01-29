package com.example.usfitness.database.record

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class RecordRepository @Inject constructor(private val recordsDAO: RecordsDAO) {

    @WorkerThread
    suspend fun addRecord(record : Record) {
        recordsDAO.insertAll(record)
    }

    @WorkerThread
    suspend fun addAllRecords(vararg records : Record){
        recordsDAO.insertAll(*records)
    }

    @WorkerThread
    suspend fun getAll() : List<Record>{
        return recordsDAO.getAll()
    }

    @WorkerThread
    fun getRecordDates(cid : Int) : LiveData<List<Record>> {
        return recordsDAO.loadAllByIds(intArrayOf(cid))
    }
}