package com.switcher.fiveg.ui.switcher

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.switcher.fiveg.domain.model.PreferredNetworkMode
import com.switcher.fiveg.util.DeviceDetector
import com.switcher.fiveg.util.SecretCodeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Switcher screen.
 * Handles network mode switching and opening system settings.
 */
@HiltViewModel
class SwitcherViewModel @Inject constructor() : ViewModel() {

    private val _selectedMode = MutableStateFlow(PreferredNetworkMode.MODE_AUTO)
    val selectedMode: StateFlow<PreferredNetworkMode> = _selectedMode.asStateFlow()

    fun selectMode(mode: PreferredNetworkMode) {
        _selectedMode.value = mode
    }

    fun getDeviceHint(): String = DeviceDetector.getDeviceHint()
    fun getDeviceModel(): String = DeviceDetector.getDeviceModel()

    /**
     * Method 1: Standard RadioInfo (Android 10 and below)
     */
    fun openMethod1(context: Context) {
        try {
            val intent = Intent().apply {
                setClassName("com.android.settings", "com.android.settings.RadioInfo")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "RadioInfo not available. Try another method.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method 2: RadioInfoControlActivity (Android 11+)
     */
    fun openMethod2(context: Context) {
        try {
            val intent = Intent().apply {
                setClassName("com.android.settings", "com.android.settings.Settings\$RadioInfoControlActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent().apply {
                    setClassName("com.android.settings", "com.android.settings.TestingSettings")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(context, "Testing settings not available. Try another method.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Samsung-specific hidden network setting activity
     */
    fun openSamsungMethod(context: Context) {
        try {
            val intent = Intent().apply {
                setClassName("com.samsung.android.app.telephonyui", "com.samsung.android.app.telephonyui.hiddennetworksetting.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Samsung method not available. Try Direct Secret Code.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method 4: Direct Secret Code - Universal method using *#*#4636#*#*
     */
    fun openDirectSecretCode(context: Context) {
        SecretCodeHelper.openDirectSecretCode(context)
    }

    /**
     * Smart method that automatically selects the best approach based on device.
     */
    fun openSmartMethod(context: Context) {
        val success = SecretCodeHelper.openNetworkSettings(context)
        if (!success) {
            SecretCodeHelper.showManualInstructions(context)
        }
    }

    /**
     * Apply selected network mode - opens system settings.
     */
    fun applyMode(context: Context) {
        SecretCodeHelper.openNetworkSettings(context)
    }
}
