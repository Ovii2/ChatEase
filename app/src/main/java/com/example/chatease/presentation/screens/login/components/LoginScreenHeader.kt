package com.example.chatease.presentation.screens.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LoginScreenHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 80.dp,
    titleStyle: TextStyle = MaterialTheme.typography.displaySmall,
    @StringRes titleText: Int,
    @StringRes labelText: Int
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier.size(logoSize),
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(titleText),
            style = titleStyle.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = stringResource(labelText),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenHeaderPreview() {
    ChatEaseTheme() {
        LoginScreenHeader(
            titleText = R.string.app_name,
            labelText = R.string.app_moto
        )
    }
}