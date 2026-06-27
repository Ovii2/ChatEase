package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneMissed
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.color
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun CallDirectionIcon(
    modifier: Modifier = Modifier,
    callDirection: CallDirection
) {
    val rotation = when (callDirection) {
        CallDirection.INCOMING -> 180f
        else -> 0f
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (callDirection == CallDirection.MISSED) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PhoneMissed,
                contentDescription = null,
                tint = callDirection.color()
            )
        } else {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Filled.Call,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = 6.dp, y = (-6).dp)
                    .rotate(rotation),
                imageVector = Icons.Filled.ArrowOutward,
                contentDescription = null,
                tint = callDirection.color()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CallDirectionIconPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CallDirectionIcon(
                    callDirection = CallDirection.OUTGOING
                )
            }
        }
    }
}