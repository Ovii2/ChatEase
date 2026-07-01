package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.data.local.datastore.user_preferences.UserPreferencesRepository
import com.example.chatease.domain.model.enums.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val timeout = 5_000L

    val themeMode = userPreferencesRepository.getCurrentTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(timeout),
            initialValue = ThemeMode.SYSTEM_DEFAULT
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setCurrentTheme(mode)
        }
    }
}