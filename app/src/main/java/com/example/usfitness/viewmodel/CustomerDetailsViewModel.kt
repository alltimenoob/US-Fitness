package com.example.usfitness.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.usfitness.database.USFitnessDatabase
import com.example.usfitness.database.customer.Customer
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.payment.PaymentRepository
import com.example.usfitness.database.record.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    usFitnessDatabase: USFitnessDatabase,
    private val customerRepository: CustomerRepository,
    recordRepository: RecordRepository,
    paymentRepository: PaymentRepository,
) : CustomerFormViewModel(
    usFitnessDatabase,
    customerRepository,
    recordRepository,
    paymentRepository
) {

    lateinit var customer : Customer

    fun loadCustomerDetails(cid: Int) {
        viewModelScope.launch {
            customer = withContext(Dispatchers.IO) {
                val customer = customerRepository.getCustomerById(cid)
                updateCustomer(
                    cid = Pair(customer.cid.toString(), false),
                    firstName = Pair(customer.firstName,false),
                    lastName = Pair(customer.lastName,false),
                    mobile = Pair(customer.mobile, false),
                    joinDate = Pair(customer.joinDate.toString(),false)
                )
                return@withContext customer
            }
        }
    }

    fun saveCustomerDetails(context : Context){

        val checkInputs = validateInputs(customerOnly = true)

        if (!checkInputs) {
            Toast.makeText(context, "Error : Provide correct information", Toast.LENGTH_SHORT)
                .show()
            return
        }


        val customer = Customer(
            cid = Integer.parseInt(this.cid.first),
            firstName = this.firstName.first,
            lastName = this.lastName.first,
            mobile = this.mobile.first,
            joinDate = LocalDate.parse(this.joinDate.first)
        )

        if(this.customer.equals(customer)){
            Toast.makeText(context,"Make changes first",Toast.LENGTH_SHORT).show()
            return
        }

        this.customer = customer

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                try{
                    customerRepository.updateCustomer(customer)
                    return@withContext "Customer Details Saved"
                } catch (e : Exception){
                    return@withContext e.localizedMessage
                }
            }
            Toast.makeText(context,response,Toast.LENGTH_SHORT).show()
        }

    }

}
