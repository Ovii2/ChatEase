package com.example.chatease.presentation.ui.screens.privacy_security.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun PrivacyAndSecuritySectionLayout(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600
        )
        Column(
            modifier = Modifier.padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PrivacyAndSecuritySectionLayoutPreview() {
    val content = List(20) {
        PrivacyAndSecurityItem(
            icon = Icons.Filled.Block,
            title = R.string.privacy_controls,
            label = R.string.people_you_have_blocked,
            onClick = {}
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrivacyAndSecuritySectionLayout(
                    title = R.string.privacy_controls,
                    content = {},
                )
            }
        }
    }
}
