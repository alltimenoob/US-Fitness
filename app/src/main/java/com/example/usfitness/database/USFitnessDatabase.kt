package com.example.usfitness.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.usfitness.database.customer.Customer
import com.example.usfitness.database.record.Record
import com.example.usfitness.database.customer.CustomerDAO
import com.example.usfitness.database.record.RecordsDAO

@Database(entities = [Customer::class, Record::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class USFitnessDatabase : RoomDatabase() {
    abstract fun customerDAO(): CustomerDAO
    abstract fun recordDao() : RecordsDAO

    companion object {
        private var instance: RoomDatabase? = null

        fun getDatabaseInstance(applicationContext: Context): USFitnessDatabase {
            if (instance != null) {
                return instance as USFitnessDatabase
            }
            synchronized(this) {
                instance = Room.databaseBuilder(
                    applicationContext,
                    USFitnessDatabase::class.java, "usfitness-db"
                ).build()
                return instance as USFitnessDatabase
            }
        }
    }
}
