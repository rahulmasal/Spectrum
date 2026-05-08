package com.switcher.fiveg.ui.switcher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.switcher.fiveg.ui.components.GlassCard

@Composable
fun SwitcherScreen(
    viewModel: SwitcherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

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
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Spectrum Switcher",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose the best method for your device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Device hint
                Text(
                    text = viewModel.getDeviceHint(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Method 1 Button
        MethodButton(
            title = "Method 1: Android 11-",
            subtitle = "For devices on Android 10 or older",
            icon = Icons.Default.Smartphone,
            onClick = { viewModel.openMethod1(context) },
            isVisible = isVisible,
            delay = 200
        )

        // Method 2 Button
        MethodButton(
            title = "Method 2: Android 11+",
            subtitle = "For modern devices (Android 11, 12, 13, 14+)",
            icon = Icons.Default.PhoneAndroid,
            onClick = { viewModel.openMethod2(context) },
            isVisible = isVisible,
            delay = 300
        )

        // Samsung Method Button
        MethodButton(
            title = "Samsung Method",
            subtitle = "Dedicated selector for Samsung Galaxy devices",
            icon = Icons.Default.Settings,
            onClick = { viewModel.openSamsungMethod(context) },
            isVisible = isVisible,
            delay = 400
        )

        // Method 4: Direct Secret Code Button
        MethodButton(
            title = "Method 4: Direct Secret Code",
            subtitle = "Universal: Auto-dials *#*#4636#*#*",
            icon = Icons.Default.Dialpad,
            onClick = { viewModel.openDirectSecretCode(context) },
            isVisible = isVisible,
            delay = 500
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Info card
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(400, 600)) + slideInVertically(tween(400, 600)) { 30 }
        ) {
            GlassCard(
                glowColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Important Guide",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                    Text(
                        text = "1. Select any method above to access hidden settings.\n" +
                                "2. In the menu, select 'Set Preferred Network Type'.\n" +
                                "3. Choose 'NR Only' for 5G only or 'LTE Only' for 4G only.\n\n" +
                                "⚠️ Warning: 'NR Only' will disable 4G/3G/2G. If 5G coverage is lost, you will have no signal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun MethodButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isVisible: Boolean,
    delay: Int
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400, delay)) + slideInVertically(tween(400, delay)) { 30 }
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
