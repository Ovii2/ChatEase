package com.example.chatease.presentation.screens.login

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.chatease.R
import com.example.chatease.presentation.screens.login.components.LoginScreenContent
import com.example.chatease.presentation.screens.login.components.LoginScreenHeader
import com.example.chatease.presentation.ui.state.LoginUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.LoginViewModel
import com.example.chatease.presentation.validation.AuthValidator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    onNavigateToSignUpScreen: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToChatScreen: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val loginUiState by loginViewModel.uiState.collectAsState()

    var rememberMe by rememberSaveable { mutableStateOf(false) }
    val rememberEmail by loginViewModel.rememberEmail.collectAsState(initial = false)
    val savedEmail by loginViewModel.savedEmail.collectAsState(initial = "")

    val failedLoginMessage = stringResource(R.string.fail_login)

    var hasTriedLogin by rememberSaveable { mutableStateOf(false) }

    val emailError = AuthValidator.validateEmail(email)
    val passwordError = AuthValidator.validatePassword(password)

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current
    val background =
        if (isSystemInDarkTheme()) R.drawable.background_login_dark else R.drawable.login_screen_background

    LaunchedEffect(loginUiState) {
        when (loginUiState) {
            is LoginUiState.Success -> {
                delay(1000)
                focusManager.clearFocus()
                loginViewModel.resetState()
                onNavigateToChatScreen()
            }

            is LoginUiState.Error -> {
                Toast.makeText(
                    context,
                    failedLoginMessage,
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.resetState()
            }

            else -> Unit
        }
    }

    LaunchedEffect(savedEmail, rememberEmail) {
        if (email.isBlank()) {
            email = savedEmail
        }
        rememberMe = rememberEmail
    }


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
                    onNavigateToSignUpScreen = onNavigateToSignUpScreen,
                    emailError = emailError,
                    showEmailError = hasTriedLogin,
                    passwordError = passwordError,
                    showPasswordError = hasTriedLogin,
                    onLoginClick = {
                        hasTriedLogin = true

                        if (emailError == null && passwordError == null) {
                            loginViewModel.login(email, password, rememberMe)
                            focusManager.clearFocus()
                        }
                    },
                    rememberMeChecked = rememberMe,
                    onRememberMeCheckChange = {
                        rememberMe = it
                        loginViewModel.onRememberMeChanged(email, it)
                    },
                    loginUiState = loginUiState
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
                    paddingValues = paddingValues,
                    onNavigateToSignUpScreen = onNavigateToSignUpScreen,
                    emailError = emailError,
                    showEmailError = hasTriedLogin,
                    passwordError = passwordError,
                    showPasswordError = hasTriedLogin,
                    onLoginClick = {
                        hasTriedLogin = true

                        if (emailError == null && passwordError == null) {
                            loginViewModel.login(email, password, rememberMe)
                            focusManager.clearFocus()
                        }
                    },
                    rememberMeChecked = rememberMe,
                    onRememberMeCheckChange = {
                        rememberMe = it
                        loginViewModel.onRememberMeChanged(email, it)
                    },
                    loginUiState = loginUiState,
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
    paddingValues: PaddingValues,
    onNavigateToSignUpScreen: () -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    passwordError: Int?,
    showPasswordError: Boolean,
    onLoginClick: () -> Unit,
    onRememberMeCheckChange: (Boolean) -> Unit,
    rememberMeChecked: Boolean,
    loginUiState: LoginUiState
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
            emailError = emailError,
            showEmailError = showEmailError,
            emailKeyboardType = KeyboardType.Email,
            emailImeAction = ImeAction.Next,
            passwordValue = password,
            passWordVisible = passwordVisible,
            onPasswordValueChange = onPasswordValueChange,
            passWordError = passwordError,
            showPassWordError = showPasswordError,
            passwordImeAction = ImeAction.Done,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            paddingValues = paddingValues,
            titleText = R.string.app_name,
            labelText = R.string.app_moto,
            onNavigateToSignUpScreen = onNavigateToSignUpScreen,
            onLoginClick = onLoginClick,
            onRememberMeCheckChange = onRememberMeCheckChange,
            rememberMeChecked = rememberMeChecked,
            loginUiState = loginUiState,
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
    @DrawableRes background: Int,
    onNavigateToSignUpScreen: () -> Unit,
    emailError: Int?,
    showEmailError: Boolean,
    passwordError: Int?,
    showPasswordError: Boolean,
    onLoginClick: () -> Unit,
    onRememberMeCheckChange: (Boolean) -> Unit,
    rememberMeChecked: Boolean,
    loginUiState: LoginUiState
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
                            emailError = emailError,
                            showEmailError = showEmailError,
                            emailKeyboardType = KeyboardType.Email,
                            emailImeAction = ImeAction.Next,
                            passwordValue = password,
                            passWordVisible = passwordVisible,
                            onPasswordValueChange = onPasswordValueChange,
                            passWordError = passwordError,
                            showPassWordError = showPasswordError,
                            passwordImeAction = ImeAction.Done,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            paddingValues = PaddingValues(),
                            headerLogoSize = 70.dp,
                            headerTitleStyle = MaterialTheme.typography.bodyLarge,
                            titleText = R.string.welcome_back,
                            labelText = R.string.login_to_continue,
                            onNavigateToSignUpScreen = onNavigateToSignUpScreen,
                            onLoginClick = onLoginClick,
                            onRememberMeCheckChange = onRememberMeCheckChange,
                            rememberMeChecked = rememberMeChecked,
                            loginUiState = loginUiState,
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
            paddingValues = PaddingValues(),
            onNavigateToSignUpScreen = {},
            emailError = 1,
            showEmailError = false,
            passwordError = 1,
            showPasswordError = false,
            onLoginClick = {},
            onRememberMeCheckChange = {},
            rememberMeChecked = false,
            loginUiState = LoginUiState.Idle,
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
            onNavigateToSignUpScreen = {},
            onLoginClick = {},
            onRememberMeCheckChange = {},
            rememberMeChecked = false,
            emailError = 1,
            showEmailError = false,
            passwordError = 1,
            showPasswordError = false,
            loginUiState = LoginUiState.Idle,
        )
    }
}