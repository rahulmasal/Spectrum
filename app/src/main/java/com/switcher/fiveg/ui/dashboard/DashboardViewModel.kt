package com.switcher.fiveg.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.switcher.fiveg.data.db.SignalHistoryEntity
import com.switcher.fiveg.data.repository.NetworkRepository
import com.switcher.fiveg.domain.model.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 * It provides real-time network status and signal history to the UI.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    /**
     * A Flow that emits the current network state (signal strength, type, carrier).
     * Collected as a StateFlow to keep the UI in sync with the latest updates.
     */
    val networkState: StateFlow<NetworkState> = networkRepository.observeNetworkState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkState()
        )

    /**
     * Exposes a list of recent signal strength readings for charting/history.
     */
    val signalHistory: StateFlow<List<SignalHistoryEntity>> =
        networkRepository.getRecentHistory(50)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
