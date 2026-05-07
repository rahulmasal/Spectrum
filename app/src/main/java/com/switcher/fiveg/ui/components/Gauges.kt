package com.switcher.fiveg.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.switcher.fiveg.ui.theme.SignalExcellent
import com.switcher.fiveg.ui.theme.SignalFair
import com.switcher.fiveg.ui.theme.SignalGood
import com.switcher.fiveg.ui.theme.SignalNone
import com.switcher.fiveg.ui.theme.SignalPoor
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated circular signal strength gauge.
 * Shows dBm value in the center with an arc that fills based on signal level.
 */
@Composable
fun SignalGauge(
    signalDbm: Int,
    signalLevel: Int,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 12.dp
) {
    // Normalize signal: typical range -50 (excellent) to -120 (no signal)
    val normalizedValue = ((signalDbm.coerceIn(-120, -50) + 120).toFloat() / 70f).coerceIn(0f, 1f)
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(normalizedValue) {
        animatedValue.animateTo(
            targetValue = normalizedValue,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val signalColor = when {
        signalLevel >= 4 -> SignalExcellent
        signalLevel == 3 -> SignalGood
        signalLevel == 2 -> SignalFair
        signalLevel == 1 -> SignalPoor
        else -> SignalNone
    }

    val glowColor = signalColor.copy(alpha = 0.3f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val arcSize = Size(
                canvasSize.width - strokeWidth.toPx() * 2,
                canvasSize.height - strokeWidth.toPx() * 2
            )
            val topLeft = Offset(strokeWidth.toPx(), strokeWidth.toPx())
            val startAngle = 150f
            val sweepAngle = 240f

            // Background track
            drawArc(
                color = signalColor.copy(alpha = 0.1f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Glow effect (wider, transparent arc behind)
            drawArc(
                color = glowColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedValue.value,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx() * 2.5f, cap = StrokeCap.Round)
            )

            // Gradient-filled arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        SignalPoor,
                        SignalFair,
                        SignalGood,
                        SignalExcellent
                    )
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedValue.value,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Tick marks
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val radius = (canvasSize.width - strokeWidth.toPx() * 2) / 2
            val tickCount = 24
            for (i in 0..tickCount) {
                val angle = Math.toRadians((startAngle + (sweepAngle * i / tickCount)).toDouble())
                val isMajor = i % 6 == 0
                val innerRadius = if (isMajor) radius - 20f else radius - 12f
                val tickWidth = if (isMajor) 2f else 1f
                val tickAlpha = if (isMajor) 0.4f else 0.2f

                val startX = center.x + innerRadius * cos(angle).toFloat()
                val startY = center.y + innerRadius * sin(angle).toFloat()
                val endX = center.x + (radius - 4f) * cos(angle).toFloat()
                val endY = center.y + (radius - 4f) * sin(angle).toFloat()

                drawLine(
                    color = signalColor.copy(alpha = tickAlpha),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth
                )
            }
        }

        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (signalDbm > -900) "$signalDbm" else "—",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = signalColor
                )
            )
            Text(
                text = "dBm",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Speed test gauge — shows Mbps during testing with animated sweep.
 */
@Composable
fun SpeedGauge(
    speedMbps: Double,
    maxSpeed: Double = 1000.0,
    label: String = "Download",
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 14.dp
) {
    val normalizedValue = (speedMbps / maxSpeed).toFloat().coerceIn(0f, 1f)
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(normalizedValue) {
        animatedValue.animateTo(
            targetValue = normalizedValue,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val arcSize = Size(
                canvasSize.width - strokeWidth.toPx() * 2,
                canvasSize.height - strokeWidth.toPx() * 2
            )
            val topLeft = Offset(strokeWidth.toPx(), strokeWidth.toPx())
            val startAngle = 135f
            val sweepAngle = 270f

            // Background track
            drawArc(
                color = color.copy(alpha = 0.08f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Glow
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedValue.value,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx() * 2f, cap = StrokeCap.Round)
            )

            // Active arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0.6f),
                        color,
                        color
                    )
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedValue.value,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "%.1f".format(speedMbps),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = "Mbps",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
        }
    }
}
