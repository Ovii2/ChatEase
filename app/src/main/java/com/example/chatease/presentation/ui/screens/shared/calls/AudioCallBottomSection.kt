package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight

@Composable
fun AudioCallBottomSection(
    modifier: Modifier = Modifier,
    callStatus: CallStatus,
    onAcceptCall: () -> Unit = {},
    onCancelCall: () -> Unit = {}
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterHorizontally)
    ) {
        val declineContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
        val acceptContainerColor =
            if (isSystemInDarkTheme()) successGreenDark else successGreenLight

        when (callStatus) {
            CallStatus.CALLING, CallStatus.CONNECTED -> {
                AudioCallBottomItem(
                    onClick = onCancelCall,
                    icon = Icons.Filled.CallEnd,
                    containerColor = declineContainerColor,
                )
            }

            CallStatus.INCOMING -> {
                AudioCallBottomItem(
                    onClick = onCancelCall,
                    icon = Icons.Filled.CallEnd,
                    containerColor = declineContainerColor

                )
                AudioCallBottomItem(
                    onClick = onAcceptCall,
                    icon = Icons.Filled.Call,
                    containerColor = acceptContainerColor
                )
            }

            CallStatus.ENDED -> {}
            else -> {}
        }
    }
}


@Composable
fun AudioCallBottomItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    containerColor: Color

) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            modifier = Modifier.size(80.dp),
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = containerColor
            )
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AudioCallBottomSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudioCallBottomSection(
                    callStatus = CallStatus.INCOMING,
                    onAcceptCall = {},
                    onCancelCall = {},
                )
            }
        }
    }
}
