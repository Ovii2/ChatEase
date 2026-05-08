package com.example.chatease.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.R
import com.example.chatease.presentation.screens.login.components.LoginScreenContent
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LoginScreenContent(
            emailValue = email,
            onEmailValueChange = { email = it },
            emailFieldPlaceholder = { Text(text = stringResource(R.string.email)) },
            emailError = 1,
            showEmailError = false,
            emailKeyboardType = KeyboardType.Email,
            emailImeAction = ImeAction.Next,
            leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
            passwordValue = password,
            passWordVisible = passwordVisible,
            onPasswordValueChange = { password = it },
            passwordFieldPlaceHolder = { Text(text = stringResource(R.string.password)) },
            passWordError = 1,
            showPassWordError = false,
            passwordKeyboardType = KeyboardType.Password,
            passwordImeAction = ImeAction.Done,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            paddingValues = paddingValues
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    ChatEaseTheme() {
        LoginScreen(
            paddingValues = PaddingValues()
        )
    }
}