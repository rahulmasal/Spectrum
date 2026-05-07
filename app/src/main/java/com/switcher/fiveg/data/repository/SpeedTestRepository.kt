package com.switcher.fiveg.data.repository

import com.switcher.fiveg.data.db.SpeedTestDao
import com.switcher.fiveg.data.db.SpeedTestResultEntity
import com.switcher.fiveg.domain.model.SpeedTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

@Singleton
class SpeedTestRepository @Inject constructor(
    private val speedTestDao: SpeedTestDao
) {
    companion object {
        // Using Cloudflare's speed test endpoints (reliable and fast)
        private const val DOWNLOAD_URL = "https://speed.cloudflare.com/__down?bytes=10000000"
        private const val UPLOAD_URL = "https://speed.cloudflare.com/__up"
        private const val PING_HOST = "1.1.1.1"
        private const val DOWNLOAD_SIZE_BYTES = 10_000_000L // 10MB
    }

    /**
     * Performs a download speed test.
     * Returns speed in Mbps.
     */
    suspend fun measureDownloadSpeed(
        onProgress: (Double) -> Unit = {}
    ): Double = withContext(Dispatchers.IO) {
        try {
            val url = URL(DOWNLOAD_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000
            connection.readTimeout = 30_000
            connection.requestMethod = "GET"

            val startTime = System.nanoTime()
            var totalBytes = 0L
            val buffer = ByteArray(8192)

            connection.inputStream.use { input ->
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    totalBytes += bytesRead
                    val progress = totalBytes.toDouble() / DOWNLOAD_SIZE_BYTES
                    onProgress(progress.coerceAtMost(1.0))
                }
            }

            val elapsedNanos = System.nanoTime() - startTime
            val elapsedSeconds = elapsedNanos / 1_000_000_000.0
            val speedMbps = (totalBytes * 8.0) / (elapsedSeconds * 1_000_000.0)

            connection.disconnect()
            speedMbps
        } catch (e: IOException) {
            0.0
        }
    }

    /**
     * Performs an upload speed test.
     * Returns speed in Mbps.
     */
    suspend fun measureUploadSpeed(
        onProgress: (Double) -> Unit = {}
    ): Double = withContext(Dispatchers.IO) {
        try {
            val dataSize = 2_000_000 // 2MB upload
            val data = ByteArray(dataSize) { (it % 256).toByte() }

            val url = URL(UPLOAD_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000
            connection.readTimeout = 30_000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.setFixedLengthStreamingMode(dataSize)

            val startTime = System.nanoTime()
            var totalSent = 0

            connection.outputStream.use { output ->
                val chunkSize = 8192
                while (totalSent < dataSize) {
                    val remaining = (dataSize - totalSent).coerceAtMost(chunkSize)
                    output.write(data, totalSent, remaining)
                    totalSent += remaining
                    onProgress(totalSent.toDouble() / dataSize)
                }
                output.flush()
            }

            // Read response to complete the request
            connection.inputStream.use { it.readBytes() }

            val elapsedNanos = System.nanoTime() - startTime
            val elapsedSeconds = elapsedNanos / 1_000_000_000.0
            val speedMbps = (totalSent * 8.0) / (elapsedSeconds * 1_000_000.0)

            connection.disconnect()
            speedMbps
        } catch (e: IOException) {
            0.0
        }
    }

    /**
     * Measures ping (latency) to a host.
     * Returns ping in milliseconds.
     */
    suspend fun measurePing(): Pair<Long, Long> = withContext(Dispatchers.IO) {
        val pings = mutableListOf<Long>()
        repeat(5) {
            try {
                val start = System.nanoTime()
                val address = InetAddress.getByName(PING_HOST)
                val reachable = address.isReachable(5000)
                val elapsed = (System.nanoTime() - start) / 1_000_000

                if (reachable) {
                    pings.add(elapsed)
                }
            } catch (e: Exception) {
                // Skip failed pings
            }
        }

        if (pings.isEmpty()) return@withContext Pair(0L, 0L)

        val avgPing = pings.average().roundToLong()
        val jitter = if (pings.size > 1) {
            val diffs = pings.zipWithNext { a, b -> kotlin.math.abs(a - b) }
            diffs.average().roundToLong()
        } else 0L

        Pair(avgPing, jitter)
    }

    /**
     * Saves a speed test result to the database.
     */
    suspend fun saveResult(result: SpeedTestResult) {
        speedTestDao.insert(
            SpeedTestResultEntity(
                downloadMbps = result.downloadMbps,
                uploadMbps = result.uploadMbps,
                pingMs = result.pingMs,
                jitterMs = result.jitterMs,
                networkType = result.networkType.name,
                serverName = result.serverName
            )
        )
    }

    /**
     * Gets all speed test results.
     */
    fun getResults(): Flow<List<SpeedTestResultEntity>> = speedTestDao.getAllResults()

    fun getRecentResults(limit: Int = 20): Flow<List<SpeedTestResultEntity>> =
        speedTestDao.getRecentResults(limit)
}
