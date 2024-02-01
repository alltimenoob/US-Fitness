package com.example.usfitness.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.payment.Payment
import com.example.usfitness.database.payment.PaymentRepository
import com.example.usfitness.database.record.DebtInfo
import com.example.usfitness.database.record.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PaymentSettlementViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    fun getCustomerName(cid: Int): LiveData<String> {
        return customerRepository.getNameById(cid)
    }

    fun getCurrentDebt(cid: Int): LiveData<Int> {
        return customerRepository.getCurrentDebt(cid)
    }

    fun getAllRecordWithDebt(cid: Int): LiveData<List<DebtInfo>> {
        return customerRepository.getAllRecordWithDebt(cid)
    }

    fun addPayment(context: Context, payment: Payment) {
        if (payment.amount == 0) {
            Toast.makeText(context, "Amount is not correct", Toast.LENGTH_SHORT).show();
            return
        }
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    paymentRepository.addPayment(payment)
                    return@withContext "Settled"
                } catch (e: Exception) {
                    return@withContext e.localizedMessage
                }
            }
            Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
        }
    }

}