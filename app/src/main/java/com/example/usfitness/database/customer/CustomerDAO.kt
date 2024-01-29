package com.example.usfitness.database.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDAO {
    @Query("SELECT * FROM customer")
    suspend fun getAll(): List<Customer>

    @Query("SELECT Customer.cid,Customer.first_name,Customer.last_name,Record.start_date,max(Record.end_date),Customer.mobile,sum(Record.paid)-sum(Record.total) FROM Customer INNER JOIN Record ON Customer.cid  == Record.cid GROUP BY Customer.cid ORDER BY max(Record.end_date)")
    fun getAllByExpiryDate() : LiveData<List<CustomizedCustomer>>

    @Query("SELECT * FROM customer WHERE cid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Customer>>

    @Query("SELECT * FROM customer WHERE first_name LIKE :firstName")
    fun findByName(firstName: String): Customer

    @Query("SELECT cid FROM customer ORDER BY cid DESC LIMIT 1")
    fun getLastId() : Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg customers: Customer)

    @Delete
    fun delete(customer : Customer)
}