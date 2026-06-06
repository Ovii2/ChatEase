package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ReactionBadge(
    modifier: Modifier = Modifier,
    reactionCounts: Map<String, Int>,
    backGroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(color = backGroundColor)
            .defaultMinSize(
                minWidth = 28.dp,
                minHeight = 14.dp
            )
            .padding(horizontal = 10.dp, vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            reactionCounts.forEach { (reaction, count) ->
                Text(
                    text = reaction,
                    fontSize = 10.sp
                )
                if (count > 1) {
                    Text(
                        text = if (count > 99) "99+" else "$count",
                        fontSize = 10.sp,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReactionBadgePreview() {
//    val reactionCounts = mapOf(
//        "❤" to 3,
//        "\uD83D\uDC4D" to 45
//    )
    val reactionCounts = mapOf(
        "❤" to 1
    )

    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReactionBadge(
                    reactionCounts = reactionCounts,
                    backGroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    textColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}