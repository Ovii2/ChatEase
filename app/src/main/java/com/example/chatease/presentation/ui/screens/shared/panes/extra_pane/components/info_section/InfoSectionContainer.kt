package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.info_section

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun InfoSectionContainer(
    modifier: Modifier = Modifier,
    @StringRes sectionTitle: Int,
    content: @Composable () -> Unit,
    showActionText: Boolean = false,
    onActionTextClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(sectionTitle),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W600
            )
            if (showActionText) {
                Text(
                    modifier = Modifier.clickable { onActionTextClick() },
                    text = stringResource(R.string.see_all),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W600
                )
            }
        }
        content()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InfoSectionContainerPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(paddingValues)
            ) {
                InfoSectionContainer(
                    sectionTitle = R.string.about,
                    showActionText = true,
                    content = {}
                )
            }
        }
    }
}