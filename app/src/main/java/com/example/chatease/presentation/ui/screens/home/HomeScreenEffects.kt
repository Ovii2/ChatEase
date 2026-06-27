package com.example.chatease.presentation.ui.screens.home

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.example.chatease.domain.model.Message
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HomeScreenEffects(
    modifier: Modifier = Modifier,
    selectedConversationId: String?,
    onLoadConversation: (String) -> Unit,
    messages: List<Message>,
    windowSizeClass: WindowSizeClass,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    onObserveMissedCallsCount: () -> Unit
) {

    LaunchedEffect(selectedConversationId) {
        selectedConversationId?.let { conversationId ->
            onLoadConversation(conversationId)
        }
    }

    LaunchedEffect(
        selectedConversationId,
        messages.size,
        windowSizeClass.widthSizeClass,
        windowSizeClass.heightSizeClass
    ) {
        delay(100.milliseconds)
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    LaunchedEffect(Unit) {
        onObserveMissedCallsCount()
    }
}