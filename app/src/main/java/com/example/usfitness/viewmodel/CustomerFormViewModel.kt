package com.example.usfitness.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.usfitness.database.customer.Customer
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.record.Record
import com.example.usfitness.database.record.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CustomerFormViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    var cid by mutableStateOf(Pair("", false))
        private set
    var mobile by mutableStateOf(Pair("", false))
        private set
    var joinDate by mutableStateOf(Pair(LocalDate.now().toString(), false))
        private set
    var paid by mutableStateOf(Pair("", false))
        private set
    var total by mutableStateOf(Pair("", false))
        private set
    var firstName by mutableStateOf(Pair("", false))
        private set
    var lastName by mutableStateOf(Pair("", false))
        private set
    var gymPackage by mutableStateOf(Pair("", false))
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val latestCID = customerRepository.getLastID()
            CoroutineScope(Dispatchers.Main).launch {
                cid = if (latestCID != null) Pair(latestCID.plus(1).toString(), false) else Pair(
                    "",
                    false
                )
            }
        }
    }

    fun updateCustomer(
        cid: Pair<String, Boolean> = this.cid,
        mobile: Pair<String, Boolean> = this.mobile,
        joinDate: Pair<String, Boolean> = this.joinDate,
        paid: Pair<String, Boolean> = this.paid,
        total: Pair<String, Boolean> = this.total,
        firstName: Pair<String, Boolean> = this.firstName,
        lastName: Pair<String, Boolean> = this.lastName,
        gymPackage: Pair<String, Boolean> = this.gymPackage
    ) {
        this.cid = cid
        this.mobile = mobile
        this.joinDate = joinDate
        this.paid = paid
        this.total = total
        this.firstName =
            Pair(
                firstName.first.replaceFirstChar { char -> char.uppercase() }.trim(),
                firstName.second
            )
        this.lastName =
            Pair(
                lastName.first.replaceFirstChar { char -> char.uppercase() }.trim(),
                lastName.second
            )
        this.gymPackage = gymPackage
    }

    private fun validateInputs(): Boolean {
        cid = Pair(cid.first, cid.first.length >= 10 || cid.first.isEmpty())
        mobile = Pair(mobile.first, mobile.first.length != 10)
        firstName = Pair(firstName.first, firstName.first.length >= 10 || firstName.first.isEmpty())
        lastName = Pair(lastName.first, lastName.first.length >= 10 || lastName.first.isEmpty())
        paid = Pair(paid.first, paid.first.contains('.') || paid.first.isEmpty())
        total = Pair(total.first, total.first.contains('.') || total.first.isEmpty())
        gymPackage = Pair(gymPackage.first, gymPackage.first.isEmpty())

        return !cid.second && !mobile.second && !firstName.second && !lastName.second && !joinDate.second && !gymPackage.second && !paid.second && !total.second
    }

    private fun clearInputs() {
        CoroutineScope(Dispatchers.IO).launch {
            val latestCID = customerRepository.getLastID()
            cid = if (latestCID != null) Pair(latestCID.plus(1).toString(), false) else Pair(
                "",
                false
            )
        }
        this.mobile = Pair("", false)
        this.joinDate = Pair(LocalDate.now().toString(), false)
        this.paid = Pair("", false)
        this.total = Pair("", false)
        this.firstName = Pair("", false)
        this.lastName = Pair("", false)
        this.gymPackage = Pair("", false)
    }

    fun addCurrentCustomer(context: Context) {

        val checkInputs = validateInputs()

        if (!checkInputs) {
            Toast.makeText(context, "Error : Provide correct information", Toast.LENGTH_SHORT)
                .show()
            CoroutineScope(Dispatchers.IO).launch {
                recordRepository.addRecord(
                    Record(
                        null,
                        514,
                        LocalDate.parse("2026-02-22"),
                        LocalDate.parse("2026-03-22"),
                        50,
                        50
                    )
                )
            }
            return
        }

        val customer = Customer(
            cid = Integer.parseInt(this.cid.first),
            firstName = this.firstName.first,
            lastName = this.lastName.first,
            mobile = this.mobile.first,
            joinDate = LocalDate.parse(this.joinDate.first)
        )

        val endDate = LocalDate.parse(this.joinDate.first)
            .plusMonths(Integer.parseInt(this.gymPackage.first.split(' ').get(0)).toLong())
        var record = Record(
            rid = null,
            Integer.parseInt(this.cid.first),
            startDate = LocalDate.parse(this.joinDate.first),
            endDate,
            Integer.parseInt(this.total.first),
            Integer.parseInt(this.paid.first)
        )
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    customerRepository.addCustomer(customer)
                    recordRepository.addRecord(record)
                    return@withContext Pair("Success : Customer Added", 200)
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