package com.example.chatease.presentation.ui.screens.shared.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(35.dp),
        shape = CircleShape,
        shadowElevation = 3.dp
    ) {
        val infiniteTransition = rememberInfiniteTransition(
            label = "dotsRotation"
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            ),
            label = "rotation"
        )

        val orbitRadiusDp by infiniteTransition.animateFloat(
            initialValue = 6f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbitRadius"
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val dotRadius = 2.5.dp.toPx()
            val orbitRadius = orbitRadiusDp.dp.toPx()
            val startingAngles = listOf(
                225f,
                315f,
                135f,
                45f
            )

            val positions = startingAngles.map { startingAngle ->
                val angle = Math.toRadians(
                    (startingAngle + rotation).toDouble()
                )
                Offset(
                    x = center.x + orbitRadius * cos(angle).toFloat(),
                    y = center.y + orbitRadius * sin(angle).toFloat()
                )
            }

            val colors = listOf(
                Color.Red,
                Color.Cyan,
                Color.Magenta,
                Color.Green
            )

            positions.forEachIndexed { index, position ->
                drawCircle(
                    color = colors[index],
                    radius = dotRadius,
                    center = position
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CustomCircularProgressIndicatorPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomCircularProgressIndicator()
            }
        }
    }
}