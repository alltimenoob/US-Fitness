package com.example.usfitness.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.usfitness.database.USFitnessDatabase
import com.example.usfitness.database.customer.Customer
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.payment.Payment
import com.example.usfitness.database.payment.PaymentRepository
import com.example.usfitness.database.record.Record
import com.example.usfitness.database.record.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExtendPackageViewModel  @Inject constructor(
    private val usFitnessDatabase: USFitnessDatabase,
    customerRepository: CustomerRepository,
    private val recordRepository: RecordRepository,
    private val paymentRepository: PaymentRepository,
) : CustomerFormViewModel(usFitnessDatabase,customerRepository,recordRepository,paymentRepository) {

    fun loadJoinDate(cid : Int){
        viewModelScope.launch {
            val record = withContext(Dispatchers.IO){
                return@withContext recordRepository.getLastRecordEndDate(cid)
            }
            updateCustomer(cid=Pair(cid.toString(),false),joinDate = Pair(record.endDate.plusDays(1).toString(),false))
        }
    }

    fun extendPackage(context: Context){

        val checkInputs = validateInputs(packageOnly = true)

        if (!checkInputs) {
            Toast.makeText(context, "Error : Provide correct information", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val endDate = LocalDate.parse(this.joinDate.first)
            .plusMonths(Integer.parseInt(this.gymPackage.first.split(' ').get(0)).toLong())
        val record = Record(
            rid = null,
            cid = Integer.parseInt(this.cid.first),
            startDate = LocalDate.parse(this.joinDate.first),
            endDate = endDate,
           total = Integer.parseInt(this.total.first),
        )
        val payment = Payment(
            pid = null,
            rid = 0,
            amount = Integer.parseInt(this.paid.first),
            date = record.startDate
        )
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    usFitnessDatabase.withTransaction {
                        val rid = recordRepository.addRecord(record)
                        paymentRepository.addPayment(
                            Payment(
                                pid = payment.pid,
                                rid = rid.toInt(),
                                amount = payment.amount,
                                payment.date
                            )
                        )
                    }
                    return@withContext Pair("Success : Package extended for customer no. ${cid.first}", 200)
                } catch (error: SQLiteConstraintException) {
                    return@withContext Pair(error.localizedMessage ?: "Unknown Error", 400)
                }
            }
            if (response.second == 200) {
                clearInputs()
            }
            Toast.makeText(context, response.first, Toast.LENGTH_SHORT).show()
        }

    }

}