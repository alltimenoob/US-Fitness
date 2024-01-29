package com.example.usfitness.hilt

import android.content.Context
import com.example.usfitness.database.customer.CustomerDAO
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.USFitnessDatabase
import com.example.usfitness.database.record.RecordRepository
import com.example.usfitness.database.record.RecordsDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideUSFitnessDatabase(@ApplicationContext context: Context) : USFitnessDatabase {
        return USFitnessDatabase.getDatabaseInstance(context)
    }

    @Provides
    fun providesCustomerDao(usFitnessDatabase: USFitnessDatabase) : CustomerDAO {
        return usFitnessDatabase.customerDAO()
    }

    @Provides
    fun provideCustomerRepository(customerDAO: CustomerDAO) : CustomerRepository {
        return CustomerRepository(customerDAO)
    }

    @Provides
    fun provideRecordDao(usFitnessDatabase: USFitnessDatabase) : RecordsDAO {
        return usFitnessDatabase.recordDao()
    }

    @Provides
    fun provideRecordRepository(recordsDAO: RecordsDAO) : RecordRepository {
        return RecordRepository(recordsDAO)
    }
}