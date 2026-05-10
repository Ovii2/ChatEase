package com.example.chatease.presentation.screens.sign_up

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.screens.sign_up.components.SignUpScreenBenefitItem
import com.example.chatease.presentation.screens.sign_up.components.SignUpScreenContent
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

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
        val maxWidth =
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 370.dp else 600.dp

        when {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                    windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact -> {
                SignUpScreenExpandedLayout(
                    background = background,
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
                    onNavigateToLoginScreen = onNavigateToLoginScreen,
                )
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
                    maxWidth = maxWidth,
                )
            }
        }
    }
}

@Composable
fun SignUpScreenExpandedLayout(
    modifier: Modifier = Modifier,
    @DrawableRes background: Int,
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
    onNavigateToLoginScreen: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(background),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 32.dp),
                contentAlignment = Alignment.Center

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        32.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(200.dp),
                            painter = painterResource(R.drawable.ic_app_logo),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.create_account),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.join_chatease_alt),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            minLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        SignUpScreenBenefitItem(
                            icon = Icons.Outlined.Security,
                            title = R.string.secure_private,
                            label = R.string.your_data_is_protected
                        )
                        SignUpScreenBenefitItem(
                            icon = Icons.Outlined.People,
                            title = R.string.connect_instantly,
                            label = R.string.message_call_collaborate
                        )
                        SignUpScreenBenefitItem(
                            icon = Icons.Outlined.Bolt,
                            title = R.string.built_for_everyone,
                            label = R.string.simple_fast
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                SignUpScreenContent(
                    modifier = Modifier.widthIn(max = 500.dp),
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
    onNavigateToLoginScreen: () -> Unit,
    maxWidth: Dp
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
                    .widthIn(max = maxWidth)
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

@Preview(showBackground = true, showSystemUi = true, name = "Compact")
@Composable
private fun SignUpScreenCompactPreview() {
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_sign_up_dark else R.drawable.background_sign_up

    ChatEaseTheme() {
        SignUpScreenCompactLayout(
            fullNameValue = "",
            onFullNameValueChange = {},
            fullNameError = 1,
            showFullNameError = false,
            emailValue = "",
            onEmailFieldChange = {},
            emailError = 1,
            showEmailError = false,
            passwordValue = "",
            onPasswordValueChange = {},
            passwordError = 1,
            showPasswordError = false,
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            confirmPasswordValue = "",
            onConfirmPasswordValueChange = {},
            confirmPasswordError = 1,
            showConfirmPasswordError = false,
            onSignUpClick = {},
            focusManager = LocalFocusManager.current,
            background = background,
            onNavigateToLoginScreen = {},
            maxWidth = 370.dp
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Compact Night",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SignUpScreenCompactNightPreview() {
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_sign_up_dark else R.drawable.background_sign_up

    ChatEaseTheme() {
        SignUpScreenCompactLayout(
            fullNameValue = "",
            onFullNameValueChange = {},
            fullNameError = 1,
            showFullNameError = false,
            emailValue = "",
            onEmailFieldChange = {},
            emailError = 1,
            showEmailError = false,
            passwordValue = "",
            onPasswordValueChange = {},
            passwordError = 1,
            showPasswordError = false,
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            confirmPasswordValue = "",
            onConfirmPasswordValueChange = {},
            confirmPasswordError = 1,
            showConfirmPasswordError = false,
            onSignUpClick = {},
            focusManager = LocalFocusManager.current,
            background = background,
            onNavigateToLoginScreen = {},
            maxWidth = 370.dp
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Expanded",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SignUpScreenExpandedPreview() {
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_sign_up_dark else R.drawable.background_sign_up

    ChatEaseTheme() {
        SignUpScreenExpandedLayout(
            background = background,
            fullNameValue = "",
            onFullNameValueChange = {},
            fullNameError = 1,
            showFullNameError = false,
            emailValue = "",
            onEmailFieldChange = {},
            emailError = 1,
            showEmailError = false,
            passwordValue = "",
            onPasswordValueChange = {},
            passwordError = 1,
            showPasswordError = false,
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            confirmPasswordValue = "",
            onConfirmPasswordValueChange = {},
            confirmPasswordError = 1,
            showConfirmPasswordError = false,
            onSignUpClick = {},
            focusManager = LocalFocusManager.current,
            onNavigateToLoginScreen = {},
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Expanded Night",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SignUpScreenExpandedNightPreview() {
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_sign_up_dark else R.drawable.background_sign_up

    ChatEaseTheme() {
        SignUpScreenExpandedLayout(
            background = background,
            fullNameValue = "",
            onFullNameValueChange = {},
            fullNameError = 1,
            showFullNameError = false,
            emailValue = "",
            onEmailFieldChange = {},
            emailError = 1,
            showEmailError = false,
            passwordValue = "",
            onPasswordValueChange = {},
            passwordError = 1,
            showPasswordError = false,
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            confirmPasswordValue = "",
            onConfirmPasswordValueChange = {},
            confirmPasswordError = 1,
            showConfirmPasswordError = false,
            onSignUpClick = {},
            focusManager = LocalFocusManager.current,
            onNavigateToLoginScreen = {},
        )
    }
}