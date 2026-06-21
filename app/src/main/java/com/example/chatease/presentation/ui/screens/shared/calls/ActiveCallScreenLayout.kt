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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun ActiveCallScreenLayout(
    modifier: Modifier = Modifier,
    callId: String,
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

    val palettes = listOf(
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.secondary
        ),
        listOf(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.tertiaryFixedDim,
            MaterialTheme.colorScheme.secondary
        ),
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.tertiaryFixedDim,
            MaterialTheme.colorScheme.primary
        )
    )

    val backgroundColor = palettes[abs(callId.hashCode()) % palettes.size]

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = backgroundColor,
                startY = offset,
                endY = offset + 1000f
            )
        ),
        contentAlignment = Alignment.TopCenter
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
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}