package com.example.usfitness.database.payment

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val paymentDAO: PaymentDAO) {

    @WorkerThread
    suspend fun getAll() : List<Payment> {
       return paymentDAO.getAll()
    }

    @WorkerThread
    fun getPaymentsForCustomer(cid : Int) : LiveData<List<Payment>> {
        return paymentDAO.getPaymentsForCustomer(cid)
    }

    @WorkerThread
    suspend fun addPayment(payment : Payment) : Long {
        return paymentDAO.insert(payment)
    }

    suspend fun addAllPayment(vararg payment : Payment) {
        return paymentDAO.insertAll(*payment)
    }
}