package com.example.chatease.presentation.screens.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    emailValue: String,
    onEmailValueChange: (String) -> Unit,
    emailFieldPlaceholder: @Composable () -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    emailKeyboardType: KeyboardType,
    emailImeAction: ImeAction,
    passwordValue: String,
    passWordVisible: Boolean,
    onPasswordValueChange: (String) -> Unit,
    passwordFieldPlaceHolder: @Composable () -> Unit,
    passWordError: Int?,
    showPassWordError: Boolean,
    passwordImeAction: ImeAction,
    onTogglePasswordVisibility: () -> Unit,
    headerLogoSize: Dp = 80.dp,
    headerTitleStyle: TextStyle = MaterialTheme.typography.displaySmall,
    @StringRes titleText: Int,
    @StringRes labelText: Int
) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LoginScreenHeader(
            logoSize = headerLogoSize,
            titleStyle = headerTitleStyle,
            titleText = titleText,
            labelText = labelText,
        )
        LoginScreenForm(
            emailValue = emailValue,
            onEmailValueChange = onEmailValueChange,
            emailFieldPlaceholder = emailFieldPlaceholder,
            emailError = emailError,
            showEmailError = showEmailError,
            emailKeyboardType = emailKeyboardType,
            emailImeAction = emailImeAction,
            passwordValue = passwordValue,
            passWordVisible = passWordVisible,
            onPasswordValueChange = onPasswordValueChange,
            passwordFieldPlaceHolder = passwordFieldPlaceHolder,
            passWordError = passWordError,
            showPassWordError = showPassWordError,
            passwordImeAction = passwordImeAction,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            rememberMeChecked = false,
            onRememberMeChecked = {},
            onLoginClick = {},
        )
        LoginScreenFooter()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenContentPreview() {
    ChatEaseTheme() {
        LoginScreenContent(
            emailValue = "email@email.com",
            onEmailValueChange = {},
            emailFieldPlaceholder = { Text(text = "Email") },
            emailError = 1,
            showEmailError = false,
            emailKeyboardType = KeyboardType.Email,
            emailImeAction = ImeAction.Next,
            passwordValue = "",
            passWordVisible = false,
            onPasswordValueChange = {},
            passwordFieldPlaceHolder = { Text(text = "Password") },
            passWordError = 1,
            showPassWordError = false,
            passwordImeAction = ImeAction.Done,
            onTogglePasswordVisibility = {},
            paddingValues = PaddingValues(),
            titleText = R.string.app_name,
            labelText = R.string.app_moto,
        )
    }
}