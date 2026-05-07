package com.switcher.fiveg.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.CellInfoGsm
import android.telephony.CellSignalStrengthNr
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.switcher.fiveg.data.db.SignalHistoryDao
import com.switcher.fiveg.data.db.SignalHistoryEntity
import com.switcher.fiveg.domain.model.DataActivity
import com.switcher.fiveg.domain.model.NetworkState
import com.switcher.fiveg.domain.model.NetworkType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signalHistoryDao: SignalHistoryDao
) {
    private val telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val executor: Executor = Executors.newSingleThreadExecutor()

    /**
     * Observes network state changes as a Flow.
     * Uses TelephonyCallback (API 31+) with fallback for older versions.
     */
    fun observeNetworkState(): Flow<NetworkState> = callbackFlow {
        var currentState = buildInitialState()
        trySend(currentState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Modern API: TelephonyCallback (API 31+)
            val callback = object : TelephonyCallback(),
                TelephonyCallback.SignalStrengthsListener,
                TelephonyCallback.DisplayInfoListener,
                TelephonyCallback.DataActivityListener,
                TelephonyCallback.CellInfoListener {

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    val dbm = extractBestDbm(signalStrength)
                    val level = signalStrength.level
                    currentState = currentState.copy(
                        signalStrengthDbm = dbm,
                        signalLevel = level
                    )
                    trySend(currentState)
                }

                override fun onDisplayInfoChanged(displayInfo: TelephonyDisplayInfo) {
                    val networkType = resolveNetworkType(displayInfo)
                    currentState = currentState.copy(
                        networkType = networkType,
                        isNrNsa = displayInfo.overrideNetworkType ==
                                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA ||
                                displayInfo.overrideNetworkType ==
                                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED
                    )
                    trySend(currentState)
                }

                override fun onDataActivity(direction: Int) {
                    currentState = currentState.copy(
                        dataActivity = when (direction) {
                            TelephonyManager.DATA_ACTIVITY_IN -> DataActivity.IN
                            TelephonyManager.DATA_ACTIVITY_OUT -> DataActivity.OUT
                            TelephonyManager.DATA_ACTIVITY_INOUT -> DataActivity.INOUT
                            TelephonyManager.DATA_ACTIVITY_DORMANT -> DataActivity.DORMANT
                            else -> DataActivity.NONE
                        }
                    )
                    trySend(currentState)
                }

                override fun onCellInfoChanged(cellInfoList: MutableList<CellInfo>) {
                    val primaryCell = cellInfoList.firstOrNull { it.isRegistered }
                    primaryCell?.let { cell ->
                        currentState = currentState.copy(
                            cellId = extractCellId(cell),
                            bandInfo = extractBandInfo(cell)
                        )
                        trySend(currentState)
                    }
                }
            }

            try {
                telephonyManager.registerTelephonyCallback(executor, callback)
            } catch (e: SecurityException) {
                // Permission not granted
            }

            // Periodically save to history
            val historyJob = launch {
                while (true) {
                    delay(30_000) // Every 30 seconds
                    saveToHistory(currentState)
                }
            }

            awaitClose {
                telephonyManager.unregisterTelephonyCallback(callback)
                historyJob.cancel()
            }
        } else {
            // Fallback: Poll-based for older devices
            val pollJob = launch {
                while (true) {
                    currentState = buildInitialState()
                    trySend(currentState)
                    delay(5_000)
                }
            }

            awaitClose {
                pollJob.cancel()
            }
        }
    }.distinctUntilChanged()

    /**
     * Builds the initial network state from current system values.
     */
    private fun buildInitialState(): NetworkState {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return NetworkState()
        }

        val carrierName = telephonyManager.networkOperatorName ?: ""
        val isRoaming = telephonyManager.isNetworkRoaming
        val operatorNumeric = telephonyManager.networkOperator ?: ""

        @Suppress("DEPRECATION")
        val networkTypeInt = try {
            telephonyManager.dataNetworkType
        } catch (e: SecurityException) {
            TelephonyManager.NETWORK_TYPE_UNKNOWN
        }

        val signalStrength = telephonyManager.signalStrength
        val dbm = signalStrength?.let { extractBestDbm(it) } ?: -999
        val level = signalStrength?.level ?: 0

        return NetworkState(
            networkType = NetworkType.fromAndroidType(networkTypeInt),
            signalStrengthDbm = dbm,
            signalLevel = level,
            carrierName = carrierName,
            isRoaming = isRoaming,
            isDataConnected = telephonyManager.dataState == TelephonyManager.DATA_CONNECTED,
            operatorNumeric = operatorNumeric
        )
    }

    /**
     * Extracts the best dBm value from signal strength.
     */
    private fun extractBestDbm(signalStrength: SignalStrength): Int {
        // Try NR (5G) first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            signalStrength.cellSignalStrengths.forEach { css ->
                if (css is CellSignalStrengthNr) {
                    val dbm = css.dbm
                    if (dbm != Int.MAX_VALUE && dbm != -1) return dbm
                }
            }
        }

        // Then try the general best signal
        val cellStrengths = signalStrength.cellSignalStrengths
        val best = cellStrengths
            .filter { it.dbm != Int.MAX_VALUE && it.dbm != -1 }
            .minByOrNull { it.dbm } // Closest to 0 = strongest
        return best?.dbm ?: -999
    }

    /**
     * Resolves the display network type including 5G NSA detection.
     */
    private fun resolveNetworkType(displayInfo: TelephonyDisplayInfo): NetworkType {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return when (displayInfo.overrideNetworkType) {
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA -> NetworkType.NR_NSA
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> NetworkType.NR_SA
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> NetworkType.NR_SA
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA -> NetworkType.LTE_CA
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> NetworkType.LTE_CA
                else -> NetworkType.fromAndroidType(displayInfo.networkType)
            }
        }
        return NetworkType.fromAndroidType(displayInfo.networkType)
    }

    /**
     * Extracts the cell ID from a CellInfo object.
     */
    private fun extractCellId(cellInfo: CellInfo): Long {
        return when (cellInfo) {
            is CellInfoLte -> cellInfo.cellIdentity.ci.toLong()
            is CellInfoNr -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    (cellInfo.cellIdentity as? android.telephony.CellIdentityNr)?.nci ?: -1L
                } else -1L
            }
            is CellInfoWcdma -> cellInfo.cellIdentity.cid.toLong()
            is CellInfoGsm -> cellInfo.cellIdentity.cid.toLong()
            else -> -1L
        }
    }

    /**
     * Extracts band information from a CellInfo object.
     */
    private fun extractBandInfo(cellInfo: CellInfo): String {
        return when (cellInfo) {
            is CellInfoLte -> {
                val earfcn = cellInfo.cellIdentity.earfcn
                if (earfcn != Int.MAX_VALUE) "EARFCN: $earfcn" else ""
            }
            is CellInfoNr -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val nci = cellInfo.cellIdentity as? android.telephony.CellIdentityNr
                    val nrarfcn = nci?.nrarfcn ?: Int.MAX_VALUE
                    if (nrarfcn != Int.MAX_VALUE) "NRARFCN: $nrarfcn" else ""
                } else ""
            }
            is CellInfoWcdma -> {
                val uarfcn = cellInfo.cellIdentity.uarfcn
                if (uarfcn != Int.MAX_VALUE) "UARFCN: $uarfcn" else ""
            }
            else -> ""
        }
    }

    /**
     * Saves the current network state to the history database.
     */
    private suspend fun saveToHistory(state: NetworkState) {
        if (state.signalStrengthDbm != -999) {
            signalHistoryDao.insert(
                SignalHistoryEntity(
                    signalDbm = state.signalStrengthDbm,
                    networkType = state.networkType.name,
                    cellId = state.cellId,
                    carrierName = state.carrierName,
                    isRoaming = state.isRoaming
                )
            )
        }
    }

    /**
     * Gets the signal history for charting.
     */
    fun getSignalHistory(since: Long): Flow<List<SignalHistoryEntity>> {
        return signalHistoryDao.getHistorySince(since)
    }

    fun getRecentHistory(limit: Int = 100): Flow<List<SignalHistoryEntity>> {
        return signalHistoryDao.getRecentHistory(limit)
    }
}
