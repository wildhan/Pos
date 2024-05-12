package com.example.pos.moduledi

import com.example.pos.db.RecordsDao
import com.example.pos.repositories.RecordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideRecordsRepository(
        recordsDao: RecordsDao
    ): RecordRepository {
        return RecordRepository(recordsDao)
    }
}