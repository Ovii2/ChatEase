package com.example.chatease

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.chatease.presentation.ui.navigation.AppNavHost
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ChatEase(modifier: Modifier = Modifier) {
    ChatEaseTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0)
        ) { innerPadding ->
            val navController = rememberNavController()
            AppNavHost(
                paddingValues = innerPadding,
                navController = navController
            )
        }
    }
}