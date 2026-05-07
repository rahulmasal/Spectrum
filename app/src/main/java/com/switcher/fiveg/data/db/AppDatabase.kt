package com.switcher.fiveg.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SignalHistoryEntity::class, SpeedTestResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signalHistoryDao(): SignalHistoryDao
    abstract fun speedTestDao(): SpeedTestDao
}
