package com.switcher.fiveg.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Helper class for accessing hidden Android network settings via secret codes.
 * Supports multiple methods with intelligent fallback based on device manufacturer.
 */
object SecretCodeHelper {

    private const val SECRET_CODE = "*#*#4636#*#*"
    private const val RADIO_INFO_CLASSIC = "com.android.settings.RadioInfo"
    private const val RADIO_INFO_11_PLUS = "com.android.settings.Settings\$RadioInfoControlActivity"
    private const val TESTING_SETTINGS = "com.android.settings.TestingSettings"
    private const val SAMSUNG_HIDDEN_NETWORK = "com.samsung.android.app.telephonyui.hiddennetworksetting.MainActivity"

    /**
     * Opens network settings using the most appropriate method for the device.
     */
    fun openNetworkSettings(context: Context) {
        val manufacturer = DeviceDetector.detectManufacturer()

        when (manufacturer) {
            DeviceDetector.DeviceManufacturer.SAMSUNG -> {
                if (!trySamsungMethod(context)) {
                    openDirectSecretCode(context)
                }
            }
            DeviceDetector.DeviceManufacturer.XIAOMI,
            DeviceDetector.DeviceManufacturer.REALME -> {
                if (!openDirectSecretCode(context)) {
                    tryMethod2(context) ?: tryMethod1(context)
                }
            }
            else -> {
                if (!tryMethod2(context)) {
                    if (!tryMethod1(context)) {
                        openDirectSecretCode(context)
                    }
                }
            }
        }
    }

    /**
     * Direct secret code method - opens dialer with *#*#4636#*#*
     */
    fun openDirectSecretCode(context: Context): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$SECRET_CODE")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No dialer app found.", Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            Toast.makeText(context, "Try manually dialing $SECRET_CODE", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun tryMethod1(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName("com.android.settings", RADIO_INFO_CLASSIC)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) { false }
    }

    private fun tryMethod2(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName("com.android.settings", RADIO_INFO_11_PLUS)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val intent = Intent().apply {
                    setClassName("com.android.settings", TESTING_SETTINGS)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            } catch (e2: Exception) { false }
        }
    }

    private fun trySamsungMethod(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName("com.samsung.android.app.telephonyui", SAMSUNG_HIDDEN_NETWORK)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) { false }
    }

    fun showManualInstructions(context: Context) {
        Toast.makeText(context, "Manual: Dial *#*#4636#*#* → Select 'Set preferred network type'", Toast.LENGTH_LONG).show()
    }
}
