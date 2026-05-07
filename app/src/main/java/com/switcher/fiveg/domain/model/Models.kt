package com.switcher.fiveg.domain.model

/**
 * Represents the various network generations/types.
 */
enum class NetworkType(val displayName: String, val generation: String) {
    NR_SA("5G SA", "5G"),
    NR_NSA("5G NSA", "5G"),
    LTE("LTE", "4G"),
    LTE_CA("LTE-A", "4G+"),
    HSPA_PLUS("HSPA+", "3.5G"),
    HSPA("HSPA", "3G"),
    WCDMA("WCDMA", "3G"),
    EDGE("EDGE", "2.5G"),
    GPRS("GPRS", "2G"),
    GSM("GSM", "2G"),
    CDMA("CDMA", "2G"),
    EVDO("EVDO", "3G"),
    UNKNOWN("Unknown", "?");

    companion object {
        fun fromAndroidType(type: Int): NetworkType {
            return when (type) {
                20 -> NR_SA       // TelephonyManager.NETWORK_TYPE_NR
                19 -> LTE_CA      // TelephonyDisplayInfo override
                13 -> LTE         // TelephonyManager.NETWORK_TYPE_LTE
                15 -> HSPA_PLUS   // TelephonyManager.NETWORK_TYPE_HSPAP
                10 -> HSPA        // TelephonyManager.NETWORK_TYPE_HSPA
                3 -> WCDMA        // TelephonyManager.NETWORK_TYPE_UMTS
                2 -> EDGE         // TelephonyManager.NETWORK_TYPE_EDGE
                1 -> GPRS         // TelephonyManager.NETWORK_TYPE_GPRS
                16 -> GSM         // TelephonyManager.NETWORK_TYPE_GSM
                4, 7 -> CDMA      // CDMA / 1xRTT
                5, 6, 12, 14 -> EVDO  // EVDO variants
                else -> UNKNOWN
            }
        }
    }
}

/**
 * Represents the preferred network mode that can be set via system settings.
 */
enum class PreferredNetworkMode(
    val displayName: String,
    val subtitle: String,
    val iconLabel: String
) {
    MODE_5G_ONLY("5G Only", "NR Only — Best speeds, may lose signal in weak areas", "5G"),
    MODE_LTE_ONLY("LTE Only", "4G LTE — Reliable coverage with good speeds", "4G"),
    MODE_3G_ONLY("3G Only", "WCDMA — Legacy mode, wider coverage", "3G"),
    MODE_2G_ONLY("2G Only", "GSM — Maximum coverage, minimal data", "2G"),
    MODE_AUTO("Auto", "System selects the best available network", "AUTO");
}

/**
 * Current state of the network connection.
 */
data class NetworkState(
    val networkType: NetworkType = NetworkType.UNKNOWN,
    val signalStrengthDbm: Int = -999,
    val signalLevel: Int = 0,        // 0-4
    val carrierName: String = "",
    val isRoaming: Boolean = false,
    val cellId: Long = -1,
    val isDataConnected: Boolean = false,
    val dataActivity: DataActivity = DataActivity.NONE,
    val subscriptionId: Int = -1,
    val simSlotIndex: Int = 0,
    val operatorNumeric: String = "",
    val bandInfo: String = "",
    val isNrNsa: Boolean = false     // 5G NSA (non-standalone) via LTE anchor
)

enum class DataActivity {
    NONE, IN, OUT, INOUT, DORMANT
}

/**
 * Speed test result.
 */
data class SpeedTestResult(
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val pingMs: Long = 0,
    val jitterMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val networkType: NetworkType = NetworkType.UNKNOWN,
    val serverName: String = ""
)

/**
 * Signal history data point.
 */
data class SignalHistoryPoint(
    val timestamp: Long,
    val signalDbm: Int,
    val networkType: NetworkType,
    val cellId: Long
)
