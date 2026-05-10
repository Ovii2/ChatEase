package com.example.chatease.presentation.screens.login

import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.screens.login.components.LoginScreenContent
import com.example.chatease.presentation.screens.login.components.LoginScreenHeader
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_login_dark else R.drawable.login_screen_background


    activity?.let { activity ->
        val windowSizeClass = calculateWindowSizeClass(activity)

        when {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                    windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact -> {
                LoginScreenExpandedLayout(
                    email = email,
                    onEmailValueChange = { email = it },
                    password = password,
                    passwordVisible = passwordVisible,
                    onPasswordValueChange = { password = it },
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                    focusManager = focusManager,
                    background = background,
                )
            }

            else -> {
                LoginScreenCompactLayout(
                    focusManager = focusManager,
                    email = email,
                    onEmailValueChange = { email = it },
                    password = password,
                    passwordVisible = passwordVisible,
                    onPasswordValueChange = { password = it },
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun LoginScreenCompactLayout(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    email: String,
    onEmailValueChange: (String) -> Unit,
    password: String,
    passwordVisible: Boolean,
    onPasswordValueChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    paddingValues: PaddingValues
) {
    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        LoginScreenContent(
            modifier = Modifier.widthIn(max = 600.dp),
            emailValue = email,
            onEmailValueChange = onEmailValueChange,
            emailError = 1,
            showEmailError = false,
            emailKeyboardType = KeyboardType.Email,
            emailImeAction = ImeAction.Next,
            passwordValue = password,
            passWordVisible = passwordVisible,
            onPasswordValueChange = onPasswordValueChange,
            passWordError = 1,
            showPassWordError = false,
            passwordImeAction = ImeAction.Done,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            paddingValues = paddingValues,
            titleText = R.string.app_name,
            labelText = R.string.app_moto,
        )
    }
}

@Composable
fun LoginScreenExpandedLayout(
    modifier: Modifier = Modifier,
    email: String,
    onEmailValueChange: (String) -> Unit,
    password: String,
    passwordVisible: Boolean,
    onPasswordValueChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    focusManager: FocusManager,
    @DrawableRes background: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                LoginScreenHeader(
                    titleText = R.string.app_name,
                    labelText = R.string.app_moto,
                    logoSize = 120.dp
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(500.dp)
                        .fillMaxHeight(0.85f),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        LoginScreenContent(
                            emailValue = email,
                            onEmailValueChange = onEmailValueChange,
                            emailError = 1,
                            showEmailError = false,
                            emailKeyboardType = KeyboardType.Email,
                            emailImeAction = ImeAction.Next,
                            passwordValue = password,
                            passWordVisible = passwordVisible,
                            onPasswordValueChange = onPasswordValueChange,
                            passWordError = 1,
                            showPassWordError = false,
                            passwordImeAction = ImeAction.Done,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            paddingValues = PaddingValues(),
                            headerLogoSize = 70.dp,
                            headerTitleStyle = MaterialTheme.typography.bodyLarge,
                            titleText = R.string.welcome_back,
                            labelText = R.string.login_to_continue,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Compact")
@Composable
private fun LoginScreenCompactPreview() {
    ChatEaseTheme() {
        LoginScreenCompactLayout(
            focusManager = LocalFocusManager.current,
            email = "",
            onEmailValueChange = {},
            password = "",
            passwordVisible = false,
            onPasswordValueChange = {},
            onTogglePasswordVisibility = {},
            paddingValues = PaddingValues()
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240", name = "Medium"
)
@Composable
private fun LoginScreenExpandedPreview() {
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_login_dark else R.drawable.login_screen_background

    ChatEaseTheme() {
        LoginScreenExpandedLayout(
            email = "",
            onEmailValueChange = {},
            password = "",
            passwordVisible = false,
            onPasswordValueChange = {},
            onTogglePasswordVisibility = {},
            focusManager = LocalFocusManager.current,
            background = background,
        )
    }
}