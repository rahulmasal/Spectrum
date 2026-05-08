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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.DARK
    )

    val dynamicColor: StateFlow<Boolean> = userPreferences.dynamicColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val notifyNetworkChange: StateFlow<Boolean> = userPreferences.notifyNetworkChange.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val notifySignalDrop: StateFlow<Boolean> = userPreferences.notifySignalDrop.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDynamicColor(enabled)
        }
    }

    fun setNotifyNetworkChange(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifyNetworkChange(enabled)
        }
    }

    fun setNotifySignalDrop(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifySignalDrop(enabled)
        }
    }
}
