package com.example.chatease.presentation.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatease.presentation.screens.login.LoginScreen
import com.example.chatease.presentation.screens.sign_up.SignUpScreen

@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {
        composable(route = Screens.SignUp.route) {
            SignUpScreen(
                onNavigateToLoginScreen = {
                    navController.navigate(Screens.Login.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screens.Login.route) {
            LoginScreen(paddingValues = paddingValues, onNavigateToSignUpScreen = {
                navController.navigate(Screens.SignUp.route) {
                    launchSingleTop = true
                }
            })
        }
    }
}