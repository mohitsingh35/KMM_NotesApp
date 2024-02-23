package com.ncs.notesapp.android.di


import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import com.ncs.notesapp.NoteDatabase
import com.ncs.notesapp.data.local.DatabaseDriverFactory
import com.ncs.notesapp.data.local.FirebaseManager
import com.ncs.notesapp.data.note.SqlDelightNoteDataSource
import com.ncs.notesapp.domain.note.NoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(app).createDriver()
    }

    @Provides
    @Singleton
    fun provideNoteDataSource(driver: SqlDriver): NoteDataSource {
        return SqlDelightNoteDataSource(NoteDatabase(driver))
    }

    @Provides
    @Singleton
    fun provideFirebaseManager():FirebaseManager{
        return FirebaseManager()
    }


}