package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ActiveCallScreenLayout(
    modifier: Modifier = Modifier,
    backgroundColors: List<Color>,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "call_background")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = backgroundColors,
                startY = offset,
                endY = offset + 1000f
            )
        )
    ) {
        Scaffold(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            containerColor = Color.Transparent,
            topBar = {
                CommonCallsTopBar()
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                content()
            }
        }
    }
}