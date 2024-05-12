package com.example.pos.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pos.data.model.local.Record

@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun RecordsDao(): RecordsDao
}
