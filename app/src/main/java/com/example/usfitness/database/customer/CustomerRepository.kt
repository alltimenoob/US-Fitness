package com.example.usfitness.database.customer

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.usfitness.database.record.DebtInfo
import javax.inject.Inject


class CustomerRepository @Inject constructor(private val customerDAO: CustomerDAO) {

    @WorkerThread
    suspend fun addCustomer(customer: Customer) {
        customerDAO.insert(customer)
    }

    @WorkerThread
    suspend fun addAllCustomer(vararg customers: Customer) {
        customerDAO.insertAll(*customers)
    }

    @WorkerThread
    suspend fun updateCustomer(customer : Customer) {
        customerDAO.updateCustomer(customer)
    }

    @WorkerThread
    fun getLastID(): Int? {
        return customerDAO.getLastId()
    }

    suspend fun getCustomerById(cid: Int): Customer {
        return customerDAO.getCustomerById(cid)
    }

    fun getNameById(cid: Int): LiveData<String> {
        return customerDAO.findById(cid)
    }

    fun getCurrentDebt(cid: Int): LiveData<Int> {
        return customerDAO.getCurrentDebt(cid)
    }

    fun getAllRecordWithDebt(cid: Int): LiveData<List<DebtInfo>> {
        return customerDAO.getAllRecordWithDebt(cid)
    }

    @WorkerThread
    suspend fun getAll(): List<Customer> {
        return customerDAO.getAll()
    }

    @WorkerThread
    fun getAllByExpiryDate(): LiveData<List<CustomizedCustomer>> {
        return customerDAO.getAllByExpiryDate()
    }

}