package com.switcher.fiveg.ui.switcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.switcher.fiveg.domain.model.PreferredNetworkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SwitcherViewModel @Inject constructor() : ViewModel() {

    private val _selectedMode = MutableStateFlow(PreferredNetworkMode.MODE_AUTO)
    val selectedMode: StateFlow<PreferredNetworkMode> = _selectedMode.asStateFlow()

    fun selectMode(mode: PreferredNetworkMode) {
        _selectedMode.value = mode
    }

    /**
     * Attempts to open the network settings where the user can change their preferred network mode.
     * Tries multiple approaches since this varies by device manufacturer.
     */
    fun applyMode(context: Context) {
        val strategies = listOf(
            // Strategy 1: RadioInfo activity (most direct)
            {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.android.settings",
                        "com.android.settings.RadioInfo"
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            },
            // Strategy 2: TestingSettings
            {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.android.settings",
                        "com.android.settings.TestingSettings"
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            },
            // Strategy 3: Testing menu via secret code
            {
                val intent = Intent("android.provider.Telephony.SECRET_CODE").apply {
                    data = android.net.Uri.parse("android_secret_code://4636")
                }
                context.sendBroadcast(intent)
                true
            },
            // Strategy 4: Standard network settings
            {
                val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            },
            // Strategy 5: Wireless settings fallback
            {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            }
        )

        for (strategy in strategies) {
            try {
                if (strategy()) return
            } catch (e: Exception) {
                continue
            }
        }

        // All strategies failed
        Toast.makeText(
            context,
            "Could not open network settings. Try dialing *#*#4636#*#* in your Phone app.",
            Toast.LENGTH_LONG
        ).show()
    }
}
