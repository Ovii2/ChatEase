package com.example.chatease.presentation.screens.shared.auth

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AuthActionButton(
    modifier: Modifier = Modifier,
    @StringRes buttonText: Int,
    isLoading: Boolean,
    isSuccess: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    colors: List<Color>
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                Brush.horizontalGradient(
                    colors = colors
                ),
                shape = RoundedCornerShape(16.dp),
            ),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            }

            isSuccess -> {
                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null)
            }

            else -> {
                Text(text = stringResource(buttonText))

            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AuthActionButtonPreview() {
    ChatEaseTheme() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AuthActionButton(
                buttonText = R.string.sign_up,
                isLoading = false,
                isSuccess = true,
                onClick = {},
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        }
    }
}