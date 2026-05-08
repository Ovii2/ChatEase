package com.example.chatease.presentation.ui.navigation

sealed class Screens(val route: String) {

    data object Login : Screens("login")
}