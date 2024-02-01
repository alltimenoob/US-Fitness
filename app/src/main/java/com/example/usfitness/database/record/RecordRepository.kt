package com.example.usfitness.database.record

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class RecordRepository @Inject constructor(private val recordsDAO: RecordsDAO) {

    @WorkerThread
    suspend fun addRecord(record : Record) : Long {
        return recordsDAO.insert(record)
    }

    @WorkerThread
    suspend fun addAllRecords(vararg records : Record){
        recordsDAO.insertALl(*records)
    }

    @WorkerThread
    suspend fun getAll() : List<Record>{
        return recordsDAO.getAll()
    }

    @WorkerThread
    suspend fun getLastRecordEndDate(cid : Int) : Record {
        return recordsDAO.getLastRecordEndDate(cid)
    }
}