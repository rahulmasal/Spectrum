package com.switcher.fiveg.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.switcher.fiveg.domain.model.DataActivity
import com.switcher.fiveg.domain.model.NetworkType
import com.switcher.fiveg.ui.components.GlassCard
import com.switcher.fiveg.ui.components.InfoRow
import com.switcher.fiveg.ui.components.NetworkTypeBadge
import com.switcher.fiveg.ui.components.SignalGauge
import com.switcher.fiveg.ui.components.getNetworkColor
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val signalHistory by viewModel.signalHistory.collectAsStateWithLifecycle()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val networkColor = getNetworkColor(networkState.networkType)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -40 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Network Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing dot indicator
                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (networkState.isDataConnected) networkColor.copy(alpha = pulseAlpha)
                                else Color.Gray,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (networkState.isDataConnected) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Signal Gauge + Network Badge
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, 200)) + slideInVertically(tween(600, 200)) { 40 }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Background glow
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .blur(60.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    networkColor.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SignalGauge(
                        signalDbm = networkState.signalStrengthDbm,
                        signalLevel = networkState.signalLevel,
                        size = 200.dp
                    )

                    NetworkTypeBadge(
                        networkType = networkState.networkType,
                        large = true
                    )
                }
            }
        }

        // Carrier Info Card
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, 400)) + slideInVertically(tween(600, 400)) { 40 }
        ) {
            GlassCard(glowColor = networkColor.copy(alpha = 0.08f)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Connection Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    InfoRow(
                        icon = Icons.Outlined.SimCard,
                        label = "Carrier",
                        value = networkState.carrierName.ifEmpty { "Unknown" }
                    )
                    InfoRow(
                        icon = Icons.Outlined.SignalCellularAlt,
                        label = "Network Type",
                        value = networkState.networkType.displayName,
                        valueColor = networkColor
                    )
                    InfoRow(
                        icon = Icons.Outlined.NetworkCheck,
                        label = "Signal Level",
                        value = "${networkState.signalLevel}/4"
                    )
                    InfoRow(
                        icon = Icons.Outlined.Public,
                        label = "Roaming",
                        value = if (networkState.isRoaming) "Yes" else "No",
                        valueColor = if (networkState.isRoaming) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                    )
                    InfoRow(
                        icon = Icons.Outlined.CellTower,
                        label = "Cell ID",
                        value = if (networkState.cellId > 0) networkState.cellId.toString() else "N/A"
                    )
                    if (networkState.bandInfo.isNotEmpty()) {
                        InfoRow(
                            icon = Icons.Outlined.Speed,
                            label = "Band",
                            value = networkState.bandInfo
                        )
                    }
                    InfoRow(
                        icon = Icons.Outlined.SwapVert,
                        label = "Data Activity",
                        value = when (networkState.dataActivity) {
                            DataActivity.IN -> "↓ Receiving"
                            DataActivity.OUT -> "↑ Sending"
                            DataActivity.INOUT -> "↕ Active"
                            DataActivity.DORMANT -> "💤 Dormant"
                            DataActivity.NONE -> "Idle"
                        }
                    )
                    if (networkState.isNrNsa) {
                        InfoRow(
                            icon = Icons.Outlined.PhoneAndroid,
                            label = "5G Mode",
                            value = "NSA (LTE Anchor)",
                            valueColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Signal History Mini Card
        AnimatedVisibility(
            visible = isVisible && signalHistory.isNotEmpty(),
            enter = fadeIn(tween(600, 600)) + slideInVertically(tween(600, 600)) { 40 }
        ) {
            GlassCard {
                Column {
                    Text(
                        text = "Recent Signal History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mini signal bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val recentPoints = signalHistory.takeLast(30)
                        recentPoints.forEach { point ->
                            val height = ((point.signalDbm.coerceIn(-120, -50) + 120).toFloat() / 70f * 56f).coerceAtLeast(4f)
                            val color = getNetworkColor(
                                NetworkType.valueOf(point.networkType)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(height.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(color, color.copy(alpha = 0.3f))
                                        ),
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${signalHistory.size} data points collected",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Bottom nav padding
    }
}
