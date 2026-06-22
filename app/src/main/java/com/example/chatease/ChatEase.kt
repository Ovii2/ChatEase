package com.example.chatease

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chatease.domain.model.enums.ThemeMode
import com.example.chatease.presentation.ui.navigation.AppNavHost
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.AppSettingsViewModel
import com.example.chatease.presentation.ui.viewmodel.AppViewModel

@Composable
fun ChatEase(
    modifier: Modifier = Modifier,
    appSettingsViewModel: AppSettingsViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val themeMode by appSettingsViewModel.themeMode.collectAsState()
    val navController = rememberNavController()
    val incomingCall by appViewModel.incomingCall.collectAsState()

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    fun setTheme() = appSettingsViewModel.setThemeMode(
        when (themeMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
        }
    )

    LaunchedEffect(Unit) {
        appViewModel.observeIncomingCall()
    }

    LaunchedEffect(incomingCall?.id) {
        incomingCall?.let { call ->
            navController.navigate(Screens.AudioCall.createRoute(call.id)) {
                launchSingleTop = true
            }
        }
    }

    ChatEaseTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0)
        ) { innerPadding ->
            AppNavHost(
                paddingValues = innerPadding,
                navController = navController,
                onThemeToggleClick = ::setTheme
            )
        }
    }
}