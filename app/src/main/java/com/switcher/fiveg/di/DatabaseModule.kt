package com.switcher.fiveg.di

import android.content.Context
import androidx.room.Room
import com.switcher.fiveg.data.db.AppDatabase
import com.switcher.fiveg.data.db.SignalHistoryDao
import com.switcher.fiveg.data.db.SpeedTestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fiveg_switcher.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSignalHistoryDao(database: AppDatabase): SignalHistoryDao {
        return database.signalHistoryDao()
    }

    @Provides
    fun provideSpeedTestDao(database: AppDatabase): SpeedTestDao {
        return database.speedTestDao()
    }
}
