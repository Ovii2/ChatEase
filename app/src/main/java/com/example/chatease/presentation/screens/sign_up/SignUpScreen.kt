package com.example.chatease.presentation.screens.sign_up

import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.screens.sign_up.components.SignUpScreenContent

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onNavigateToLoginScreen: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current

    val background =
        if (isSystemInDarkTheme()) R.drawable.background_sign_up_dark else R.drawable.background_sign_up

    activity?.let { activity ->
        val windowSizeClass = calculateWindowSizeClass(activity)

        when {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                    windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact -> {
                SignUpScreenExpandedLayout()
            }

            else -> {
                SignUpScreenCompactLayout(
                    fullNameValue = fullName,
                    onFullNameValueChange = { fullName = it },
                    fullNameError = 1,
                    showFullNameError = false,
                    emailValue = email,
                    onEmailFieldChange = { email = it },
                    emailError = 1,
                    showEmailError = false,
                    passwordValue = password,
                    onPasswordValueChange = { password = it },
                    passwordError = 1,
                    showPasswordError = false,
                    isPasswordVisible = passwordVisible,
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                    confirmPasswordValue = confirmPassword,
                    onConfirmPasswordValueChange = { confirmPassword = it },
                    confirmPasswordError = 1,
                    showConfirmPasswordError = false,
                    onSignUpClick = {},
                    focusManager = focusManager,
                    background = background,
                    onNavigateToLoginScreen = onNavigateToLoginScreen,
                )
            }
        }
    }
}

@Composable
fun SignUpScreenExpandedLayout(modifier: Modifier = Modifier) {

}

@Composable
fun SignUpScreenCompactLayout(
    modifier: Modifier = Modifier,
    fullNameValue: String,
    onFullNameValueChange: (String) -> Unit,
    fullNameError: Int?,
    showFullNameError: Boolean,
    emailValue: String,
    onEmailFieldChange: (String) -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    passwordValue: String,
    onPasswordValueChange: (String) -> Unit,
    passwordError: Int?,
    showPasswordError: Boolean,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    confirmPasswordValue: String,
    onConfirmPasswordValueChange: (String) -> Unit,
    confirmPasswordError: Int?,
    showConfirmPasswordError: Boolean,
    onSignUpClick: () -> Unit,
    focusManager: FocusManager,
    @DrawableRes background: Int,
    onNavigateToLoginScreen: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }, contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.matchParentSize(),
            painter = painterResource(background),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(130.dp),
                painter = painterResource(R.drawable.ic_app_logo),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))

            SignUpScreenContent(
                modifier = Modifier
                    .widthIn(max = 350.dp)
                    .heightIn(max = 700.dp),
                fullNameValue = fullNameValue,
                onFullNameValueChange = onFullNameValueChange,
                fullNameError = fullNameError,
                showFullNameError = showFullNameError,
                emailValue = emailValue,
                onEmailFieldChange = onEmailFieldChange,
                emailError = emailError,
                showEmailError = showEmailError,
                passwordValue = passwordValue,
                onPasswordValueChange = onPasswordValueChange,
                passwordError = passwordError,
                showPasswordError = showPasswordError,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                confirmPasswordValue = confirmPasswordValue,
                onConfirmPasswordValueChange = onConfirmPasswordValueChange,
                confirmPasswordError = confirmPasswordError,
                showConfirmPasswordError = showConfirmPasswordError,
                onSignUpClick = onSignUpClick,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
            )
        }
    }
}