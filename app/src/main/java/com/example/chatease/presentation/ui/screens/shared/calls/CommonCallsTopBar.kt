package com.example.chatease.presentation.ui.screens.shared.calls

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonCallsTopBar(
    modifier: Modifier = Modifier,
    @StringRes title: Int? = null
) {
    TopAppBar(
        title = { title?.let { stringResource(title) } },
        navigationIcon = {
            Icon(
                modifier = modifier.size(32.dp),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}