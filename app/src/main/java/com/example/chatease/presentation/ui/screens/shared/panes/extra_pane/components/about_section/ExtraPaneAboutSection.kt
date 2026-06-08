package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.about_section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.info_section.InfoSectionContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneAboutSection(modifier: Modifier = Modifier) {
    InfoSectionContainer(
        modifier = modifier,
        sectionTitle = R.string.about,
        content = {
            Text(
                text = stringResource(
                    R.string.about_placeholder,
                    stringResource(R.string.app_name)
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExtraPaneAboutSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(paddingValues)
            ) {
                ExtraPaneAboutSection()
            }
        }
    }
}