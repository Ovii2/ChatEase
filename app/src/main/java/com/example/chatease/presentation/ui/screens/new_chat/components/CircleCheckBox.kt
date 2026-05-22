package com.example.chatease.presentation.ui.screens.new_chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircleCheckBox(
    selected: Boolean,
    enabled: Boolean = true,
    onChecked: () -> Unit
) {
    val imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Circle
    val tint =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.7f
        )
    val background = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent

    IconButton(
        onClick = { onChecked() },
        modifier = Modifier.offset(x = 4.dp, y = 4.dp),
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.background(color = background),
            imageVector = imageVector,
            tint = tint,
            contentDescription = null
        )
    }
}
