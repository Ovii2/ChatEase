package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.data.local.datastore.user_preferences.UserPreferencesRepository
import com.example.chatease.domain.model.enums.ThemeMode
import com.example.chatease.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AppSettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userPreferencesRepository: UserPreferencesRepository = mock()
    private lateinit var viewModel: AppSettingsViewModel


    @Before
    fun setUp() {
        viewModel = AppSettingsViewModel(
            userPreferencesRepository = userPreferencesRepository
        )
    }

    @Test
    fun `should set theme mode`() = runTest {
        viewModel.setThemeMode(ThemeMode.LIGHT)

        advanceUntilIdle()
        verify(userPreferencesRepository).setCurrentTheme(ThemeMode.LIGHT)
    }

}