package com.switcher.fiveg.ui.speedtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.switcher.fiveg.data.db.SpeedTestResultEntity
import com.switcher.fiveg.data.repository.NetworkRepository
import com.switcher.fiveg.data.repository.SpeedTestRepository
import com.switcher.fiveg.domain.model.SpeedTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TestPhase { IDLE, PING, DOWNLOAD, UPLOAD, DONE }

data class SpeedTestUiState(
    val phase: TestPhase = TestPhase.IDLE,
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val pingMs: Long = 0,
    val jitterMs: Long = 0,
    val progress: Double = 0.0,
    val currentSpeed: Double = 0.0,
    val isRunning: Boolean = false
)

@HiltViewModel
class SpeedTestViewModel @Inject constructor(
    private val speedTestRepository: SpeedTestRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeedTestUiState())
    val uiState: StateFlow<SpeedTestUiState> = _uiState.asStateFlow()

    val testHistory: StateFlow<List<SpeedTestResultEntity>> =
        speedTestRepository.getRecentResults(20)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun startTest() {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            _uiState.value = SpeedTestUiState(isRunning = true, phase = TestPhase.PING)

            // Phase 1: Ping
            val (ping, jitter) = speedTestRepository.measurePing()
            _uiState.value = _uiState.value.copy(
                pingMs = ping,
                jitterMs = jitter,
                phase = TestPhase.DOWNLOAD
            )

            // Phase 2: Download
            val downloadSpeed = speedTestRepository.measureDownloadSpeed { progress ->
                _uiState.value = _uiState.value.copy(
                    progress = progress,
                    currentSpeed = progress * 100 // Rough estimate for animation
                )
            }
            _uiState.value = _uiState.value.copy(
                downloadMbps = downloadSpeed,
                progress = 0.0,
                phase = TestPhase.UPLOAD
            )

            // Phase 3: Upload
            val uploadSpeed = speedTestRepository.measureUploadSpeed { progress ->
                _uiState.value = _uiState.value.copy(
                    progress = progress,
                    currentSpeed = progress * 50
                )
            }
            _uiState.value = _uiState.value.copy(
                uploadMbps = uploadSpeed,
                phase = TestPhase.DONE,
                isRunning = false
            )

            // Save result
            speedTestRepository.saveResult(
                SpeedTestResult(
                    downloadMbps = downloadSpeed,
                    uploadMbps = uploadSpeed,
                    pingMs = ping,
                    jitterMs = jitter,
                    serverName = "Cloudflare"
                )
            )
        }
    }

    fun reset() {
        _uiState.value = SpeedTestUiState()
    }
}
