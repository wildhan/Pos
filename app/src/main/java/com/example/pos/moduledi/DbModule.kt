package com.example.pos.moduledi

import android.app.Application
import androidx.room.Room
import com.example.pos.db.MainDatabase
import com.example.pos.db.RecordsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    @Singleton
    fun provideDatabase(
        application: Application,
    ): MainDatabase {
        return Room.databaseBuilder(application, MainDatabase::class.java, "CashFlow.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecordsDao(mainDatabase: MainDatabase): RecordsDao {
        return mainDatabase.RecordsDao()
    }

}