package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlinx.coroutines.delay

@Composable
fun ChatReactionRow(
    modifier: Modifier = Modifier,
    onReactionClick: (String) -> Unit
) {
    var visibleCount by remember { mutableIntStateOf(0) }

    val reactions = listOf(
        "\uD83D\uDC4D",
        "\u2764\uFE0F",
        "\uD83D\uDE02",
        "\uD83D\uDE2E",
        "\uD83D\uDE22",
        "\uD83D\uDE4F"
    )

    LaunchedEffect(Unit) {
        reactions.indices.forEach { index ->
            delay(30)

            visibleCount = index + 1
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                reactions.forEachIndexed { index, reaction ->
                    AnimatedVisibility(
                        visible = index < visibleCount,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Text(
                            modifier = Modifier.clickable { onReactionClick(reaction) },
                            text = reaction,
                            fontSize = 24.sp
                        )
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
private fun ChatReactionRowPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChatReactionRow(
                    onReactionClick = {}
                )
            }
        }
    }
}
