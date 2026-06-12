package com.example.chatease.presentation.ui.screens.shared.user

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockUserBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onBlockClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.block_user_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = stringResource(R.string.block_user_message),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss_btn),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(onClick = onBlockClick) {
                        Text(text = stringResource(R.string.block))
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun BlockUserBottomSheetPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BlockUserBottomSheet(
                    onDismiss = {},
                    onBlockClick = {}
                )
            }
        }
    }
}
