package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.data.local.datasource.AudioCallActionsDataSource
import com.example.chatease.domain.model.AudioCallActionItem
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun AudioCallActionSection(modifier: Modifier = Modifier, callStatus: CallStatus) {
    val actions = when (callStatus) {
        CallStatus.CALLING -> AudioCallActionsDataSource.callingActionItems
        CallStatus.INCOMING -> AudioCallActionsDataSource.incomingCallActions
        CallStatus.CONNECTED -> AudioCallActionsDataSource.activeCallActions
        CallStatus.ENDED -> emptyList()
    }

    val firstRow = actions.take(3)
    val secondRow = actions.drop(3)

    when (actions.size) {
        1 -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                actions.forEach { action ->
                    AudioCallActionItem(onClick = {}, item = action)
                }
            }
        }

        3 -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                actions.forEach { action ->
                    AudioCallActionItem(onClick = {}, item = action)
                }
            }
        }

        6 -> {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    firstRow.forEach { action ->
                        AudioCallActionItem(onClick = {}, item = action)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    secondRow.forEach { action ->
                        AudioCallActionItem(onClick = {}, item = action)
                    }
                }
            }
        }
    }

}

@Composable
fun AudioCallActionItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    item: AudioCallActionItem
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = CircleShape
                )
                .size(65.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = item.icon,
                contentDescription = null
            )
        }
        Text(
            text = stringResource(item.label),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AudioCallActionSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudioCallActionSection(
                    callStatus = CallStatus.CONNECTED
                )
            }
        }
    }
}
