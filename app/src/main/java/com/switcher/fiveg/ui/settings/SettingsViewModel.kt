package com.switcher.fiveg.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.switcher.fiveg.data.preferences.ThemeMode
import com.switcher.fiveg.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 * It connects the UI with UserPreferences to manage app-wide settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    /**
     * Exposes the current theme mode (Light, Dark, or System) as a StateFlow.
     */
    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.DARK
    )

    /**
     * Exposes whether dynamic colors (Material You) are enabled.
     */
    val dynamicColor: StateFlow<Boolean> = userPreferences.dynamicColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    /**
     * Exposes whether the user wants notifications when the network type changes.
     */
    val notifyNetworkChange: StateFlow<Boolean> = userPreferences.notifyNetworkChange.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    /**
     * Exposes whether the user wants notifications when the signal drops.
     */
    val notifySignalDrop: StateFlow<Boolean> = userPreferences.notifySignalDrop.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Updates the application theme mode.
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    /**
     * Enables or disables Material You dynamic colors.
     */
    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDynamicColor(enabled)
        }
    }

    /**
     * Updates the preference for network change notifications.
     */
    fun setNotifyNetworkChange(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifyNetworkChange(enabled)
        }
    }

    /**
     * Updates the preference for signal drop notifications.
     */
    fun setNotifySignalDrop(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifySignalDrop(enabled)
        }
    }
}
