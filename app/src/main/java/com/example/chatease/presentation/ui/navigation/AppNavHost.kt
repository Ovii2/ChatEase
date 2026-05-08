package com.example.chatease.presentation.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatease.presentation.screens.login.LoginScreen

@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Login.route
    ) {
        composable(route = Screens.Login.route) {
            LoginScreen(paddingValues = paddingValues)
        }
    }
}