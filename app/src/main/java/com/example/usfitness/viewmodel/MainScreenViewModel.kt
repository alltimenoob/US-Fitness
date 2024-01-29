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

    val returnFile : MutableLiveData<File> = MutableLiveData()

    init {
        filteredCustomers.addSource(customers) {
            filteredCustomers.postValue(it)
            performEverything()
        }
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

    fun getCustomerPaymentDates(cid: Int): LiveData<List<Record>> {
        return recordRepository.getRecordDates(cid)
    }

    fun saveDatabaseToFile(path: File) {

        val mainDir = File(path, "USFITNESS_PACK")
        mainDir.mkdirs()

        val customerFile = File(mainDir, "customers.csv")
        val recordsFile = File(mainDir, "records.csv")
        val zipFile = File(path, "database_${System.currentTimeMillis()}.zip")

        viewModelScope.launch {
            val customers = async { customerRepository.getAll() }.await()
            val records = async { recordRepository.getAll() }.await()

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
                        "${it.rid},${it.cid},${it.startDate},${it.endDate},${it.total},${it.paid}\n".toByteArray()
                    }.forEach { record ->
                        stream.write(record)
                    }
                }
            }

            ZipUtil.pack(File(mainDir.absolutePath), File(zipFile.absolutePath))

            returnFile.postValue(zipFile)
        }
    }

    fun getDatabaseFile() : LiveData<File> = returnFile

    fun restoreToDatabase(path: File, zipFile: File) {
        val outputDir = File(path, "USFITNESS_UNPACK")
        try{
            ZipUtil.unpack(zipFile, File(path, "USFITNESS_UNPACK"))

            val customersCSV = File(outputDir.absolutePath, "customers.csv")
            val recordsCSV = File(outputDir.absolutePath, "records.csv")

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
                val (rid, cid, startDate, endDate, total, paid ) = it.split(
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
                    Integer.parseInt(paid)
                )
            }.toList()

            viewModelScope.launch {
                async { customerRepository.addAllCustomer(*customers.toTypedArray()) }.await()
                async { recordRepository.addAllRecords(*records.toTypedArray()) }.await()
            }

            outputDir.deleteRecursively()
        }
        catch(exception: ZipException){
            Log.e(  "CustomizedError",exception.message ?: "")
            outputDir.delete()
        }


    }
}

private operator fun <E> List<E>.component6() = get(5)



