package com.switcher.fiveg.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, DARK, LIGHT }

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val NOTIFY_NETWORK_CHANGE = booleanPreferencesKey("notify_network_change")
        private val NOTIFY_SIGNAL_DROP = booleanPreferencesKey("notify_signal_drop")
        private val SIGNAL_THRESHOLD = intPreferencesKey("signal_threshold")
        private val MONITORING_ENABLED = booleanPreferencesKey("monitoring_enabled")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.DARK.name)
    }

    val notifyNetworkChange: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFY_NETWORK_CHANGE] ?: true
    }

    val notifySignalDrop: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFY_SIGNAL_DROP] ?: false
    }

    val signalThreshold: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[SIGNAL_THRESHOLD] ?: -110
    }

    val monitoringEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MONITORING_ENABLED] ?: false
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DYNAMIC_COLOR] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun setNotifyNetworkChange(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_NETWORK_CHANGE] = enabled }
    }

    suspend fun setNotifySignalDrop(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_SIGNAL_DROP] = enabled }
    }

    suspend fun setSignalThreshold(dbm: Int) {
        context.dataStore.edit { it[SIGNAL_THRESHOLD] = dbm }
    }

    suspend fun setMonitoringEnabled(enabled: Boolean) {
        context.dataStore.edit { it[MONITORING_ENABLED] = enabled }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[DYNAMIC_COLOR] = enabled }
    }
}
