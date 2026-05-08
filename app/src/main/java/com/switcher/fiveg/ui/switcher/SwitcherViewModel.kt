package com.switcher.fiveg.ui.switcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.switcher.fiveg.domain.model.PreferredNetworkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Switcher screen.
 * It handles the selection of network modes and triggers the opening of system settings.
 */
@HiltViewModel
class SwitcherViewModel @Inject constructor() : ViewModel() {

    private val _selectedMode = MutableStateFlow(PreferredNetworkMode.MODE_AUTO)
    /**
     * Exposes the currently selected network mode in the UI.
     */
    val selectedMode: StateFlow<PreferredNetworkMode> = _selectedMode.asStateFlow()

    /**
     * Updates the selected network mode state.
     */
    fun selectMode(mode: PreferredNetworkMode) {
        _selectedMode.value = mode
    }

    /**
     * Method 1: Target for Android 11 and below.
     * Most devices use the standard RadioInfo activity.
     */
    fun openMethod1(context: Context) {
        try {
            val intent = Intent()
            intent.setClassName("com.android.settings", "com.android.settings.RadioInfo")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            fallback(context)
        }
    }

    /**
     * Method 2: Target for Android 11 and above.
     * Some manufacturers changed the entry point or added restrictions.
     */
    fun openMethod2(context: Context) {
        try {
            val intent = Intent()
            intent.setClassName("com.android.settings", "com.android.settings.Settings\$RadioInfoControlActivity")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            // Try another common 11+ variant
            try {
                val intent = Intent()
                intent.setClassName("com.android.settings", "com.android.settings.TestingSettings")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e2: Exception) {
                fallback(context)
            }
        }
    }

    /**
     * Method for Samsung devices.
     * Samsung often blocks the standard RadioInfo but has its own secret menu.
     */
    fun openSamsungMethod(context: Context) {
        try {
            val intent = Intent()
            intent.setClassName("com.samsung.android.app.telephonyui", "com.samsung.android.app.telephonyui.hiddennetworksetting.MainActivity")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            fallback(context)
        }
    }

    /**
     * Universal fallback using the dialer secret code.
     */
    private fun fallback(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:*#*#4636#*#*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "If settings didn't open, manually dial *#*#4636#*#*", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open settings on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * General apply method that tries to be smart based on OS version.
     */
    fun applyMode(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openMethod2(context)
        } else {
            openMethod1(context)
        }
    }
}
