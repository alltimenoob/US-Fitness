package com.example.usfitness.database.customer

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject


class CustomerRepository @Inject constructor(private val customerDAO: CustomerDAO) {


    @WorkerThread
    suspend fun addCustomer(customer: Customer) {
        customerDAO.insertAll(customer)
    }

    @WorkerThread
    suspend fun addAllCustomer(vararg customers : Customer) {
        customerDAO.insertAll(*customers)
    }

    @WorkerThread
    fun getLastID() : Int?{
        return customerDAO.getLastId()
    }

    @WorkerThread
    suspend fun getAll() : List<Customer> {
        return customerDAO.getAll()
    }

    @WorkerThread
    fun getAllByExpiryDate() : LiveData<List<CustomizedCustomer>> {
        return customerDAO.getAllByExpiryDate()
    }

}