package com.example.usfitness.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usfitness.database.customer.Customer
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.customer.CustomizedCustomer
import com.example.usfitness.database.payment.Payment
import com.example.usfitness.database.payment.PaymentRepository
import com.example.usfitness.database.record.Record
import com.example.usfitness.database.record.RecordRepository
import com.example.usfitness.ui.theme.getColor
import com.example.usfitness.viewmodel.helper.FilterMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.zip.ZipException
import javax.inject.Inject

enum class Sort(val text: String) {
    ByName("By Name"),
    ByRegistration("By Registration"),
    ByExpiryDate("By Expiry Date")
}

enum class SortState(val value: Int) {
    Ascending(0),
    Descending(1)
}

data class SortMenu(val current: Sort, var state: SortState)

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val recordRepository: RecordRepository,
    private val paymentRepository: PaymentRepository
) :
    ViewModel() {

    private var customers: LiveData<List<CustomizedCustomer>> =
        customerRepository.getAllByExpiryDate()

    private val filteredCustomers: MediatorLiveData<List<CustomizedCustomer>> =
        MediatorLiveData()

    private var searchQuery: String = ""

    private var filterMenuList: List<FilterMenu> = listOf(
        FilterMenu("Active", getColor("Green"), true),
        FilterMenu("Close To Expiry", getColor("Yellow"), true),
        FilterMenu("Expired", getColor("Red"), false)
    )

    private var sortChoice: SortMenu = SortMenu(Sort.ByExpiryDate, SortState.Ascending)

    private val returnFile : MutableLiveData<File> = MutableLiveData()

    init {
        filteredCustomers.addSource(customers) {
            filteredCustomers.postValue(it)
            performEverything()
        }
    }

    fun searchCustomer(searchQuery: String) {
        this.searchQuery = searchQuery
        performEverything()
    }

    fun filterCustomer(filterMenuList: List<FilterMenu>) {
        this.filterMenuList = filterMenuList
        performEverything()
    }

    fun sortCustomer(sort: SortMenu) {
        this.sortChoice = sort
        performEverything()
    }

    private fun <T, R : Comparable<R>> Iterable<T>.sortAccordingToState(selector: (T) -> R?): List<T> {
        return when (sortChoice.state) {
            SortState.Ascending -> this.sortedBy(selector)
            else -> this.sortedByDescending(selector)
        }
    }


    private fun performEverything() {
        val customerList = (customers.value ?: emptyList())

        val searchedList = customerList.filter { customer ->
            (customer.firstName + " " + customer.lastName).lowercase()
                .contains(searchQuery.lowercase())
        }

        val filteredList = searchedList.filter { customer ->
            getExpiryTintForCustomer(customer.endDate) in (filterMenuList.filter { it.selected }
                .map { it.color })
        }

        val sortedList =
            when (sortChoice.current) {
                Sort.ByExpiryDate -> filteredList.sortAccordingToState { it.endDate }
                Sort.ByName -> filteredList.sortAccordingToState { it.firstName + it.lastName }
                else -> filteredList.sortAccordingToState { it.cid }
            }

        filteredCustomers.postValue(sortedList)
    }

    fun getAllByExpiryDate(): LiveData<List<CustomizedCustomer>> {
        return filteredCustomers
    }

    fun getDatabaseFile() : LiveData<File> = returnFile


    fun getExpiryTintForCustomer(endDate: LocalDate): Color {
        return if (endDate.isBefore(
                LocalDate.now()
            )
        ) getColor("Red") else if (endDate.isBefore(
                LocalDate.now().plusDays(10)
            )
        ) getColor("Yellow")
        else getColor("Green")
    }

    fun getPaymentsForCustomer(cid: Int): LiveData<List<Payment>> {
        return paymentRepository.getPaymentsForCustomer(cid)
    }

    fun saveDatabaseToFile(path: File) {

        val mainDir = File(path, "USFITNESS_PACK")
        mainDir.mkdirs()

        val customerFile = File(mainDir, "customers.csv")
        val recordsFile = File(mainDir, "records.csv")
        val paymentsFile = File(mainDir, "payments.csv")
        val zipFile = File(path, "database_${System.currentTimeMillis()}.usdb")

        viewModelScope.launch {
            val customers = async { customerRepository.getAll() }.await()
            val records = async { recordRepository.getAll() }.await()
            val payments = async { paymentRepository.getAll() }.await()

            withContext(Dispatchers.IO) {
                FileOutputStream(customerFile).use { stream ->
                    customers.map {
                        "${it.cid},${it.firstName},${it.lastName},${it.joinDate},${it.mobile}\n".toByteArray()
                    }.forEach { customer ->
                        stream.write(customer)
                    }
                }

                FileOutputStream(recordsFile).use { stream ->
                    records.map {
                        "${it.rid},${it.cid},${it.startDate},${it.endDate},${it.total}\n".toByteArray()
                    }.forEach { record ->
                        stream.write(record)
                    }
                }

                FileOutputStream(paymentsFile).use {stream ->
                    payments.map {
                        "${it.pid},${it.rid},${it.amount},${it.date}\n".toByteArray()
                    }.forEach{ payment->
                        stream.write(payment)
                    }
                }
            }

            ZipUtil.pack(File(mainDir.absolutePath), File(zipFile.absolutePath))

            returnFile.postValue(zipFile)
        }
    }

    private operator fun <E> List<E>.component6() = get(5)

    fun restoreToDatabase(path: File, zipFile: File) {
        val outputDir = File(path, "USFITNESS_UNPACK")
        try{
            ZipUtil.unpack(zipFile, File(path, "USFITNESS_UNPACK"))

            val customersCSV = File(outputDir.absolutePath, "customers.csv")
            val recordsCSV = File(outputDir.absolutePath, "records.csv")
            val paymentsCSV = File(outputDir.absolutePath, "payments.csv")

            if (!(customersCSV.exists() && recordsCSV.exists())) return

            val customerInputStream = FileInputStream(customersCSV)

            val customerReader = customerInputStream.bufferedReader()
            val customers: List<Customer> = customerReader.lineSequence().map {
                val (cid, firstName, lastName, joinDate, mobile) = it.split(
                    ',',
                    ignoreCase = false,
                    limit = 5
                )
                Customer(Integer.parseInt(cid), firstName, lastName, mobile, LocalDate.parse(joinDate))
            }.toList()

            val recordInputStream = FileInputStream(recordsCSV)

            val recordReader = recordInputStream.bufferedReader()
            val records: List<Record> = recordReader.lineSequence().map {
                val (rid, cid, startDate, endDate, total ) = it.split(
                    ',',
                    ignoreCase = false,
                    limit = 6
                )

                Record(
                    Integer.parseInt(rid),
                    Integer.parseInt(cid),
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    Integer.parseInt(total),
                )
            }.toList()

            val paymentInputStream = FileInputStream(paymentsCSV)

            val paymentReader = paymentInputStream.bufferedReader()
            val payments: List<Payment> = paymentReader.lineSequence().map {
                val (pid, rid, amount,date) = it.split(
                    ',',
                    ignoreCase = false,
                    limit = 6
                )
                Payment(Integer.valueOf(pid),Integer.valueOf(rid),Integer.valueOf(amount),LocalDate.parse(date))
            }.toList()

            viewModelScope.launch {
                async { customerRepository.addAllCustomer(*customers.toTypedArray()) }.await()
                async { recordRepository.addAllRecords(*records.toTypedArray()) }.await()
                async { paymentRepository.addAllPayment(*payments.toTypedArray()) }.await()
            }

            outputDir.deleteRecursively()
        }
        catch(exception: ZipException){
            Log.e(  "CustomizedError",exception.message ?: "")
            outputDir.delete()
        }
    }
}




