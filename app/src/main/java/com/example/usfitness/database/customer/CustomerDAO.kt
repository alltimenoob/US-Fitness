package com.example.usfitness.database.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.usfitness.database.record.DebtInfo

@Dao
interface CustomerDAO {
    @Query("SELECT * FROM customer")
    suspend fun getAll(): List<Customer>

    @Query("SELECT * FROM CUSTOMER WHERE cid LIKE :cid")
    suspend fun getCustomerById(cid : Int) : Customer

    @Query("SELECT Customer.cid,Customer.first_name,Customer.last_name,Payment.date,max(Record.end_date),sum(Payment.amount)-(sum(Record.total)/count(Payment.pid)),Customer.mobile FROM Customer INNER JOIN Record ON Record.cid LIKE Customer.cid INNER JOIN Payment ON Payment.rid LIKE Record.rid GROUP BY Customer.cid")
    fun getAllByExpiryDate() : LiveData<List<CustomizedCustomer>>

    @Query("SELECT * FROM customer WHERE cid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Customer>>

    @Query("SELECT first_name  || ' ' || last_name FROM customer WHERE cid LIKE :cid")
    fun findById(cid: Int): LiveData<String>

    @Query("SELECT Sum(total - paid) FROM (SELECT Sum(payment.amount) AS paid, record.total AS total,Record.rid FROM payment INNER JOIN record ON record.rid LIKE payment.rid WHERE record.cid LIKE :cid GROUP BY payment.rid) ")
    fun getCurrentDebt(cid : Int) : LiveData<Int>

    @Query("SELECT cid FROM customer ORDER BY cid DESC LIMIT 1")
    fun getLastId() : Int?

    @Query("SELECT Record.rid, Record.end_date, sum(Payment.amount)-Record.total  FROM Record INNER JOIN Payment ON Record.rid LIKE Payment.rid WHERE Record.cid LIKE :cid GROUP BY Record.rid ORDER BY sum(Payment.amount)-Record.total")
    fun getAllRecordWithDebt(cid: Int): LiveData<List<DebtInfo>>

    @Update
    suspend fun updateCustomer(customers: Customer)

    @Insert
    suspend fun insert(customers: Customer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg customers: Customer)

    @Delete
    fun delete(customer : Customer)
}