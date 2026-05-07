package com.switcher.fiveg.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SignalHistoryEntity)

    @Query("SELECT * FROM signal_history WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getHistorySince(since: Long): Flow<List<SignalHistoryEntity>>

    @Query("SELECT * FROM signal_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 100): Flow<List<SignalHistoryEntity>>

    @Query("DELETE FROM signal_history WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM signal_history")
    suspend fun getCount(): Int
}

@Dao
interface SpeedTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: SpeedTestResultEntity)

    @Query("SELECT * FROM speed_test_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<SpeedTestResultEntity>>

    @Query("SELECT * FROM speed_test_results ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 20): Flow<List<SpeedTestResultEntity>>

    @Query("DELETE FROM speed_test_results WHERE id = :id")
    suspend fun delete(id: Long)
}
