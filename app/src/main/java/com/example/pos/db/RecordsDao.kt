package com.example.pos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos.data.model.local.Record

@Dao
interface RecordsDao {
    @Query("DELETE FROM Record")
    fun deleteRecords()

    @Query("DELETE FROM Record WHERE id == :id")
    suspend fun deleteRecordById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(record: Record)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRecord(record: Record)

    @Query("SELECT * FROM Record WHERE date BETWEEN :from AND :to")
    suspend fun getListRecord(from: Long, to: Long): List<Record>?

    @Query("SELECT * FROM Record")
    suspend fun getAllListRecord(): List<Record>?

    @Query("SELECT * FROM Record WHERE id == :id")
    suspend fun getRecordById(id: Int): List<Record>?

}