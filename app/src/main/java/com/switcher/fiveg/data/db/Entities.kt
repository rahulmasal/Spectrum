package com.switcher.fiveg.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_history")
data class SignalHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val signalDbm: Int,
    val networkType: String,
    val cellId: Long,
    val carrierName: String,
    val isRoaming: Boolean
)

@Entity(tableName = "speed_test_results")
data class SpeedTestResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Long,
    val jitterMs: Long,
    val networkType: String,
    val serverName: String
)
