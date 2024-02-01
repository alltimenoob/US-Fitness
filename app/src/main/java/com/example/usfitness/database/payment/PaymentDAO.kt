package com.example.usfitness.database.payment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaymentDAO {

    @Query("SELECT * FROM PAYMENT")
    suspend fun getAll() : List<Payment>

    @Query("SELECT Payment.pid,Payment.rid,Payment.amount,Payment.date FROM Payment INNER JOIN Record ON Record.rid LIKE Payment.rid INNER JOIN Customer ON Record.cid LIKE Customer.cid WHERE Customer.cid LIKE :cid ORDER BY date DESC")
    fun getPaymentsForCustomer(cid : Int) : LiveData<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg payment: Payment)

}