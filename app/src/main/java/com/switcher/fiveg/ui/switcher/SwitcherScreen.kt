package com.switcher.fiveg.ui.switcher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.switcher.fiveg.domain.model.PreferredNetworkMode
import com.switcher.fiveg.ui.components.GlassCard
import com.switcher.fiveg.ui.components.NetworkModeCard
import kotlinx.coroutines.delay

@Composable
fun SwitcherScreen(
    viewModel: SwitcherViewModel = hiltViewModel()
) {
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -40 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Network Mode",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select your preferred network type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Network mode cards
        PreferredNetworkMode.entries.forEachIndexed { index, mode ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, 150 + index * 80)) +
                        slideInVertically(tween(400, 150 + index * 80)) { 30 }
            ) {
                NetworkModeCard(
                    title = mode.displayName,
                    subtitle = mode.subtitle,
                    iconLabel = mode.iconLabel,
                    isSelected = selectedMode == mode,
                    onClick = { viewModel.selectMode(mode) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Apply button
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(400, 700)) + slideInVertically(tween(400, 700)) { 30 }
        ) {
            Button(
                onClick = { viewModel.applyMode(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Open Network Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Info card
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(400, 800)) + slideInVertically(tween(400, 800)) { 30 }
        ) {
            GlassCard(
                glowColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "How it works",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                    Text(
                        text = "This will open your device's network settings where you can manually select the preferred network mode. " +
                                "The availability of specific modes depends on your device manufacturer and carrier.\n\n" +
                                "⚠️ Forcing a specific mode (e.g., 5G Only) in an area without coverage will result in no signal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
